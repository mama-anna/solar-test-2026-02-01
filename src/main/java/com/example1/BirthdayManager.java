package com.example1;

import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BirthdayManager {
    private static final String DB_URL = "jdbc:sqlite:birthday.db";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS birthdays (name TEXT PRIMARY KEY, birthdate DATE)";
    private static final Scanner scanner = new Scanner(System.in, "CP866");

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute(CREATE_TABLE_SQL);
            System.out.println("База данных подключена.");

            while (true) {
                showMenu();
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        showAllRecords(conn);
                        break;
                    case 2:
                        showUpcomingBirthdays(conn);
                        break;
                    case 3:
                        addBirthday(conn);
                        break;
                    case 4:
                        deleteBirthday(conn);
                        break;
                    case 5:
                        editBirthday(conn);
                        break;
                    case 6:
                        System.out.println("Выход из программы...");
                        return;
                    default:
                        System.out.println("Неверный выбор, попробуйте еще раз.");
                }
                System.out.println("  ");

            }

        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    private static void showMenu() {
        System.out.println("\nВыберите действие:");
        System.out.println("1. Все записи");
        System.out.println("2. Ближайшие дни рождения");
        System.out.println("3. Добавить день рождения");
        System.out.println("4. Удалить день рождения");
        System.out.println("5. Редактировать день рождения");
        System.out.println("6. Выход");
        System.out.print("> ");
    }

    private static void showAllRecords(Connection conn) {
        String query = "SELECT * FROM birthdays";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Все записи:");
            while (rs.next()) {
                String name = rs.getString("name");
                String birthdate = rs.getString("birthdate");
                System.out.println(name + " - " + birthdate);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении записей: " + e.getMessage());
        }
    }

    private static void showUpcomingBirthdays(Connection conn) {
        LocalDate today = LocalDate.now();
        String query = "SELECT * FROM birthdays WHERE strftime('%m-%d', birthdate) >= strftime('%m-%d', ?) ORDER BY strftime('%m-%d', birthdate) LIMIT 5";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, today.toString());
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Ближайшие дни рождения:");
            while (rs.next()) {
                String name = rs.getString("name");
                String birthdate = rs.getString("birthdate");
                System.out.println(name + " - " + birthdate);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении ближайших дней рождения: " + e.getMessage());
        }
    }

    private static void addBirthday(Connection conn) {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите дату рождения (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        String insertSQL = "INSERT INTO birthdays (name, birthdate) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.executeUpdate();
            System.out.println("День рождения добавлен.");
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении: " + e.getMessage());
        }
    }

    private static void deleteBirthday(Connection conn) {
        System.out.print("Введите имя для удаления: ");
        String name = scanner.nextLine();

        String deleteSQL = "DELETE FROM birthdays WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("День рождения удален.");
            } else {
                System.out.println("Имя не найдено.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении: " + e.getMessage());
        }
    }

    private static void editBirthday(Connection conn) {
        System.out.print("Введите имя для редактирования: ");
        String name = scanner.nextLine();
        System.out.print("Введите новую дату рождения (YYYY-MM-DD): ");
        String newDate = scanner.nextLine();

        String updateSQL = "UPDATE birthdays SET birthdate = ? WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, newDate);
            pstmt.setString(2, name);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("День рождения обновлен.");
            } else {
                System.out.println("Имя не найдено.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при редактировании: " + e.getMessage());
        }
    }
}