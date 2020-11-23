package controller;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller {
    private View view;//объект представления
    private HTMLDocument document;
    private File currentFile;

    public Controller(View view) {
        this.view = view;
    }

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();
    }

    public void init() {
        createNewDocument();
    }

    public void exit() {
        System.exit(0);
    }

    public HTMLDocument getDocument() {
        return document;
    }

    //будет сбрасывать текущий документ.
    public void resetDocument() {
        //Удалять у текущего document слушателя правок которые можно отменить/вернуть
        if (document != null) {
            document.removeUndoableEditListener(view.getUndoListener());
        }
        //создание нового документа по умолчанию
        document = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        //добавлене слушателя
        document.addUndoableEditListener(view.getUndoListener());
        view.update();
    }

    //записывать переданный текст с html тегами в документ document.
    public void setPlainText(String text) {
        resetDocument();
        StringReader reader = new StringReader(text);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        try {
            editorKit.read(reader, document, 0);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    //получать текст из документа со всеми html тегами
    public String getPlainText() {
        StringWriter writer = new StringWriter();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        try {
            editorKit.write(writer, document, 0, document.getLength());
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
        return writer.toString();
    }

    public void createNewDocument() {
        view.selectHtmlTab();//выбираем HTML вкладку
        resetDocument();//сбрасываем текущий документ
        view.setTitle("HTML редактор");//меняем заглавие окна
        view.resetUndo();//сбрасываем правки
        currentFile = null;//обнуляем переменную
    }

    public void openDocument() {
        view.selectHtmlTab();
        JFileChooser jFileChooser = new JFileChooser();
        HTMLFileFilter fileFilter = new HTMLFileFilter();
        jFileChooser.setFileFilter(fileFilter);
        jFileChooser.setDialogTitle("Open File");
        int result = jFileChooser.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = jFileChooser.getSelectedFile();//сохраняем выбранный файл
            resetDocument();//сбрасывать документ
            view.setTitle(currentFile.getName());//даем имя заглавию пресдтавления
            try {
                FileReader fileReader = new FileReader(currentFile);
                HTMLEditorKit editorKit = new HTMLEditorKit();
                editorKit.read(fileReader, document, 0);
                view.resetUndo();//сбрасываем правки
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }

    public void saveDocument() {
        view.selectHtmlTab();
        if (currentFile != null) {
            try {
                FileWriter fileWriter = new FileWriter(currentFile);
                HTMLEditorKit editorKit = new HTMLEditorKit();
                editorKit.write(fileWriter, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        } else {
            saveDocumentAs();
        }
    }

    public void saveDocumentAs() {
        view.selectHtmlTab();//выбираем HTML вкладку
        JFileChooser jFileChooser = new JFileChooser();//новый объект для выбора файла
        HTMLFileFilter fileFilter = new HTMLFileFilter();
        jFileChooser.setFileFilter(fileFilter);//Устанавливать ему в качестве фильтра объект
        jFileChooser.setDialogTitle("Save File");//диалоговое окно
        int result = jFileChooser.showSaveDialog(view);//передаем место, гдк будем отображать
        //если файл выбран:
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = jFileChooser.getSelectedFile();//Сохранять выбранный файл
            view.setTitle(currentFile.getName());//Устанавливать имя файла в качестве заголовка окна представления
            try {
                FileWriter fileWriter = new FileWriter(currentFile);//записываем
                HTMLEditorKit editorKit = new HTMLEditorKit();
                editorKit.write(fileWriter, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }
}
