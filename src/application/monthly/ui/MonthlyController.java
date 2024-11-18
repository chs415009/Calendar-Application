package application.monthly.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;

public class MonthlyController {
    @FXML
    private Label monthLabel;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private HBox headerBox;

    private YearMonth currentYearMonth;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        updateCalendar();
    }

    @FXML
    public void setStage(Stage stage) {
        stage.setMaximized(true); // 設定視窗最大化
    }

    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();

        // 月份標題
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        monthLabel.setFont(Font.font(30)); // 調整字體大小

        // 添加星期幾標籤
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayOfWeekLabel = new Label(daysOfWeek[i]);
            dayOfWeekLabel.setStyle("-fx-font-size: 20; -fx-alignment: center;");
            calendarGrid.add(dayOfWeekLabel, i, 0);
        }

        // 計算每月的天數與第一天是星期幾
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int row = 1, column = dayOfWeek;

        // 設定格子的固定大小
        double cellSize = 80.0;

        // 填充日期格子
        for (int day = 1; day <= daysInMonth; day++) {
            // 使用 StackPane 包裹日期數字，並將數字置於左上角
            StackPane cell = new StackPane();
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-border-color: black; -fx-alignment: top-left; -fx-font-size: 18;");
            dayLabel.setPrefSize(cellSize, cellSize);
            cell.getChildren().add(dayLabel);

            // 設置格子的樣式，使其可以容納日程
            cell.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-pref-width: " + cellSize + "; -fx-pref-height: " + cellSize + ";");
            calendarGrid.add(cell, column, row);

            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }

        // 填補空白格子，並畫上斜線
        for (int i = 0; i < 7; i++) {
            // 填充頭部的空格子
            if (i < dayOfWeek) {
                addEmptyCell(i, 1);
            }
            // 填充尾部的空格子
            if (column <= 6 && column > 0 && (daysInMonth + dayOfWeek) % 7 != 0) {
                addEmptyCell(column, row);
                column++;
            }
        }
    }

    private void addEmptyCell(int column, int row) {
        // 用 Label 填充空白格子
        Label emptyLabel = new Label(" ");
        emptyLabel.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-font-size: 14;");
        emptyLabel.setPrefSize(80, 80);
        calendarGrid.add(emptyLabel, column, row);

        // 添加斜線
        Line line = new Line(10, 10, 70, 70); // 斜線從左上到右下
        line.setStroke(Color.GRAY);
        calendarGrid.add(line, column, row);
    }
}
