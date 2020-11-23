package controller;

//обработчик исключительных ситуаций
public class ExceptionHandler extends Throwable {
    public static void log(Exception e) {
        System.out.println(e.toString());
    }
}
