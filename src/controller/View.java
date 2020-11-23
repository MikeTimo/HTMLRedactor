package controller;

import listeners.FrameListener;
import listeners.TabbedPaneChangeListener;
import listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame implements ActionListener {
    private Controller controller;
    private JTabbedPane tabbedPane = new JTabbedPane();//панель с двумя вкладками
    private JTextPane htmlTextPane = new JTextPane();//компонент для визуального редактирования html
    private JEditorPane plainTextPane = new JEditorPane();//компонент для редактирования html виде текста, отображает код
    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);

    public View(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            ExceptionHandler.log(e);
        }
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    //будет вызваться при выборе пунктов меню, у которых наше представление указано в виде слушателя событий
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Новый": controller.createNewDocument();
            break;
            case "Открыть": controller.openDocument();
            break;
            case "Сохранить": controller.saveDocument();
            break;
            case  "Сохранить как...": controller.saveDocumentAs();
            break;
            case "Выход": controller.exit();
            break;
            case "О программе": showAbout();
            break;
        }
    }

    public void init() {
        initGui();
        FrameListener frameListener = new FrameListener(this);
        addWindowListener(frameListener);//Добавить слушателя событий нашего окна
        setVisible(true);
    }

    public void exit() {
        controller.exit();
    }

    public void initMenuBar() {//инициализация меню
        JMenuBar jMenuBar = new JMenuBar();//панель меню
        MenuHelper.initFileMenu(this, jMenuBar);//инициализация файла
        MenuHelper.initEditMenu(this, jMenuBar);//инициализация редактирования
        MenuHelper.initStyleMenu(this, jMenuBar);//инициализация стиля
        MenuHelper.initAlignMenu(this, jMenuBar);//инициализация выравнивания
        MenuHelper.initColorMenu(this, jMenuBar);//инициализация цвета
        MenuHelper.initFontMenu(this, jMenuBar);//инициализация шрифта
        MenuHelper.initHelpMenu(this, jMenuBar);//инициализация помощи
        getContentPane().add(jMenuBar, BorderLayout.NORTH);
    }

    public void initEditor() {//инициализация панелей редактора
        htmlTextPane.setContentType("text/html");//устанавливаем тип контента
        JScrollPane jScrollPane = new JScrollPane(htmlTextPane);//панель прокрутки
        tabbedPane.add("HTML", jScrollPane);
        JScrollPane scrollPane = new JScrollPane(plainTextPane);
        tabbedPane.add("Текст", scrollPane);
        tabbedPane.setPreferredSize(new Dimension(800, 800));//установка предпочтительного размера
        TabbedPaneChangeListener tabbedPaneChangeListener = new TabbedPaneChangeListener(this);
        tabbedPane.addChangeListener(tabbedPaneChangeListener);//устанавливать его в качестве слушателя изменений
        getContentPane().add(tabbedPane, BorderLayout.CENTER);//добавление по центру панели контента текущего фрейма нашей панели с вкладками
    }

    public void initGui() {//инициализация графического интрефейса
        initMenuBar();
        initEditor();
        pack();//устанавливает предпочтительный размер окна
    }

    //произошла смена выбранной вкладки
    public void selectedTabChanged() {
        //нужно получить текст из plainTextPane и установить его в контроллер
        if(tabbedPane.getSelectedIndex() == 0) {
            controller.setPlainText(plainTextPane.getText());
        //необходимо получить текст у контроллера с помощью метода getPlainText() и установить его в панель plainTextPane
        } else if(tabbedPane.getSelectedIndex() == 1) {
            plainTextPane.setText(controller.getPlainText());
        }
        //Сбросить правки
        resetUndo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    //отменяет последнеее действие
    public void undo() {
        try {
            undoManager.undo();
        }catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    //возвращает ранее отменненое действие
    public void redo() {
        try {
            undoManager.redo();
        }catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }

    public void resetUndo() {
        undoManager.discardAllEdits();
    }

    public boolean isHtmlTabSelected() {
        if (tabbedPane.getSelectedIndex() == 0) {
            return true;
        } else {
            return false;
        }
    }

    //выбирать вкладку и сбрасывать все правки.
    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0);
        resetUndo();
    }

    public void update() {
        htmlTextPane.setDocument(controller.getDocument());
    }

    public void showAbout() {
        JOptionPane.showMessageDialog(null, "Program for work with HTML documents", "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
