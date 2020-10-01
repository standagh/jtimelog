/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.hatua.jtimelog;

//import lombok.extern.slf4j.Slf4j;
import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.hatua.jtimelog.logs.Logs;
import cz.hatua.jtimelog.cats.Cat;
import cz.hatua.jtimelog.cats.Cats;
import cz.hatua.jtimelog.logs.EntriesChangedNotificationListener;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author standa
 *
 * Responsibility: - GUI - format output to user - change displayed day - read
 * new message and pass to controller - provides list of defined categories
 */
public class JTimeLog extends javax.swing.JFrame implements EntriesChangedNotificationListener {

    static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(JTimeLog.class);

    Logs ctrlLogs;
    Cats ctrlCats;
    FormatDayController ctrlFormat;

    ActionViewToday actionViewToday;
    ActionViewNextDay actionViewNextDay;
    ActionViewPrevDay actionViewPrevDay;
    ActionViewNextWeek actionViewNextWeek;
    ActionViewPrevWeek actionViewPrevWeek;
    ActionChangeFormatDetail actionChangeFormatDetail;
    ActionChangeFormatDetailSorted actionChangeFormatDetailSorted;
    ActionChangeFormatSummary actionChangeFormatSummary;

    
    TimerSinceLastMsgTask timerSinceLastMsgTask;
    
    /**
     * Creates new form JTimeLog
     */
    public JTimeLog() {
        log.debug("initializing components");

        initComponents();

        DefaultTableModel tasksModel = new DefaultTableModel(0, 2);
        tasksModel.setColumnCount(0);
        //tasksModel.addColumn("Group");
        tasksModel.addColumn("Task");
        tasksModel.addColumn("Note");
        //tasksModel.addRow(new String[] {"MDT", "MDT_Issue", "Issue" });
        tasksTbl.setModel(tasksModel);

        ctrlFormat = new FormatDayController();
        
        Font f = tasksTbl.getFont().deriveFont(Font.ITALIC);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            Font font = f;

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                setFont(font);
                return this;
            }

        };
        // doesn't work because the default renderer's font is reset
        // to the table's font always
        // r.setFont(font);
        // set the custom renderer for first column
        tasksTbl.getColumnModel().getColumn(1).setCellRenderer(r);
        //tasksTbl.setCellSelectionEnabled(false);
        tasksTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // no editing
        tasksTbl.setDefaultEditor(Object.class, null);
        
        logsViewTA.setEditable(false);
        
        try {
            ctrlLogs = new Logs();
            ctrlCats = new Cats();
        } catch (JTimeLogException e) {
            log.error("Error while initializing JTimelog", e);
            throw new RuntimeException("Error while initializing JTimelog", e);
        }

        actionViewToday = new ActionViewToday("View Today", new ImageIcon(getClass().getClassLoader().getResource("icons/view_today.png")), KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK), ctrlLogs, displayDayL);
        jMenuItemToday.setAction(actionViewToday);
        goDayTodayB.setAction(actionViewToday);
        goDayTodayB.setHideActionText(true);

        actionViewNextDay = new ActionViewNextDay("View Next Day", new ImageIcon(getClass().getClassLoader().getResource("icons/view_next_day.png")), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), ctrlLogs, displayDayL);
        jMenuItemNextDay.setAction(actionViewNextDay);
        goDayNextB.setAction(actionViewNextDay);
        goDayNextB.setHideActionText(true);

        actionViewPrevDay = new ActionViewPrevDay("View Previous Day", new ImageIcon(getClass().getClassLoader().getResource("icons/view_prev_day.png")), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK), ctrlLogs, displayDayL);
        jMenuItemPreviousDay.setAction(actionViewPrevDay);
        goDayPrevB.setAction(actionViewPrevDay);
        goDayPrevB.setHideActionText(true);

        actionViewNextWeek = new ActionViewNextWeek("View Next Week", null, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), ctrlLogs, displayDayL);
        jMenuItemNextWeek.setAction(actionViewNextWeek);

        actionViewPrevWeek = new ActionViewPrevWeek("View Previous Week", null, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), ctrlLogs, displayDayL);
        jMenuItemPreviousWeek.setAction(actionViewPrevWeek);

        actionChangeFormatDetail = new ActionChangeFormatDetail("Change Format Detail", null, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK), ctrlFormat, this);
        jMenuItemFormatDetail.setAction(actionChangeFormatDetail);

        actionChangeFormatDetailSorted = new ActionChangeFormatDetailSorted("Change Format Detail Sorted", null, KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK), ctrlFormat, this);
        jMenuItemFormatDetailSorted.setAction(actionChangeFormatDetailSorted);

        actionChangeFormatSummary = new ActionChangeFormatSummary("Change Format Summary", null, KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK), ctrlFormat, this);
        jMenuItemFormatSummary.setAction(actionChangeFormatSummary);

        jCBReportType.removeAllItems();
        for(String it: GTLSReport.getReportTypes()) {
            jCBReportType.addItem(it);
        }
        
        populateCats();

        ctrlLogs.setNotificationTarget(this);
        ctrlLogs.sendEntriesChangedNotification(ctrlLogs.getDisplayedDay());
        updateDisplaydDayLabel();
        log.debug("Initialization done");
    }

    private class TimerSinceLastMsgTask extends SwingWorker<Void, Long> {
        public final LocalDateTime lastEntry;
        
        TimerSinceLastMsgTask(LocalDateTime lastEntry) {
            if(lastEntry == null) {
                throw new NullPointerException("lastEntry is null");
            }
            this.lastEntry = lastEntry;
        }
        
        @Override
        protected Void doInBackground() {
            while (!isCancelled()) {
                publish(ChronoUnit.MINUTES.between(lastEntry, LocalDateTime.now()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            return null;
        }

        @Override
        protected void process(List<Long> minutesSinceEntry) {
            long mins = minutesSinceEntry.get(minutesSinceEntry.size()-1);
            String hourMinutes = String.format("%02d:%02d", mins / 60, mins % 60);
            String currTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            currentTValL.setText(currTime + "  (" + hourMinutes + ")");
        }
    }

    private void populateCats() {
        Map<String, List<Cat>> allCats = ctrlCats.getAllCats();
        DefaultTableModel tasksModel = (DefaultTableModel)tasksTbl.getModel();
        tasksModel.setRowCount(0);
        /*
        for (String g : allCats.keySet()) {
            for (Cat c : allCats.get(g)) {
        */
        allCats.keySet().forEach((g) -> {
            allCats.get(g).forEach((c) -> {
                //tasksModel.addRow(new String[] {c.getGroup(), c.getCat(), c.getNote() });
                tasksModel.addRow(new String[]{c.getCat(), c.getNote()});
            });
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logsViewTA = new javax.swing.JTextArea();
        jTFReport = new javax.swing.JTextField();
        jCBReportType = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tasksTbl = new javax.swing.JTable();
        currentTValL = new javax.swing.JLabel();
        taskEdit = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        goDayPrevB = new javax.swing.JButton();
        displayDayL = new javax.swing.JLabel();
        goDayNextB = new javax.swing.JButton();
        goDayTodayB = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuView = new javax.swing.JMenu();
        jMenuItemToday = new javax.swing.JMenuItem();
        jMenuItemPreviousDay = new javax.swing.JMenuItem();
        jMenuItemNextDay = new javax.swing.JMenuItem();
        jMenuItemPreviousWeek = new javax.swing.JMenuItem();
        jMenuItemNextWeek = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFormatDetail = new javax.swing.JMenuItem();
        jMenuItemFormatDetailSorted = new javax.swing.JMenuItem();
        jMenuItemFormatSummary = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemEditTasks = new javax.swing.JMenuItem();
        jMenuItemEditCategories = new javax.swing.JMenuItem();
        jMenuItemReload = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerSize(11);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.W_RESIZE_CURSOR));

        jLabel1.setText("Report:");

        logsViewTA.setColumns(20);
        logsViewTA.setFont(new java.awt.Font("Ubuntu Mono", 0, 16)); // NOI18N
        logsViewTA.setRows(5);
        jScrollPane1.setViewportView(logsViewTA);

        jTFReport.setText("w");
        jTFReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFReportActionPerformed(evt);
            }
        });

        jCBReportType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jCBReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTFReport))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTFReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBReportType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        tasksTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tasksTbl.setGridColor(new java.awt.Color(234, 234, 234));
        tasksTbl.setRowHeight(23);
        tasksTbl.setRowMargin(5);
        tasksTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tasksTblMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tasksTbl);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        currentTValL.setFont(new java.awt.Font("Ubuntu Mono", 0, 16)); // NOI18N
        currentTValL.setText("00:00 (00:00)");
        currentTValL.setName(""); // NOI18N

        taskEdit.setName("enterLogTextField"); // NOI18N
        taskEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskEditActionPerformed(evt);
            }
        });

        jToolBar1.setRollover(true);

        goDayPrevB.setText("<");
        goDayPrevB.setFocusable(false);
        goDayPrevB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goDayPrevB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(goDayPrevB);

        displayDayL.setText("jLabel1");
        jToolBar1.add(displayDayL);

        goDayNextB.setText(">");
        goDayNextB.setFocusable(false);
        goDayNextB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goDayNextB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(goDayNextB);

        goDayTodayB.setText(">|");
        goDayTodayB.setFocusable(false);
        goDayTodayB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goDayTodayB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(goDayTodayB);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenuView.setText("View");

        jMenuItemToday.setText("Today");
        jMenuView.add(jMenuItemToday);

        jMenuItemPreviousDay.setText("Previous Day");
        jMenuView.add(jMenuItemPreviousDay);

        jMenuItemNextDay.setText("Next Day");
        jMenuView.add(jMenuItemNextDay);

        jMenuItemPreviousWeek.setText("Previous Week");
        jMenuView.add(jMenuItemPreviousWeek);

        jMenuItemNextWeek.setText("Next Week");
        jMenuView.add(jMenuItemNextWeek);
        jMenuView.add(jSeparator2);

        jMenuItemFormatDetail.setText("Format Detail");
        jMenuView.add(jMenuItemFormatDetail);

        jMenuItemFormatDetailSorted.setText("Format Sorted");
        jMenuItemFormatDetailSorted.setToolTipText("");
        jMenuView.add(jMenuItemFormatDetailSorted);

        jMenuItemFormatSummary.setText("Format Summary");
        jMenuView.add(jMenuItemFormatSummary);
        jMenuView.add(jSeparator1);

        jMenuBar1.add(jMenuView);

        jMenuTools.setText("Tools");
        jMenuTools.setToolTipText("");

        jMenuItemEditTasks.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemEditTasks.setText("Edit tasks");
        jMenuItemEditTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditTasksActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemEditTasks);

        jMenuItemEditCategories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemEditCategories.setText("Edit Categories");
        jMenuItemEditCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditCategoriesActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemEditCategories);

        jMenuItemReload.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemReload.setText("Reload data");
        jMenuItemReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReloadActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemReload);

        jMenuBar1.add(jMenuTools);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(currentTValL, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(taskEdit))
                    .addComponent(jSplitPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentTValL)
                    .addComponent(taskEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void taskEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taskEditActionPerformed
        setNewLogMsg();
    }//GEN-LAST:event_taskEditActionPerformed

    private void jMenuItemEditTasksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditTasksActionPerformed
        openFile(new File(ctrlLogs.getTasksFileName()));
    }//GEN-LAST:event_jMenuItemEditTasksActionPerformed

    private void jMenuItemEditCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditCategoriesActionPerformed
        openFile(new File(ctrlCats.getCategoriesFileName()));
    }//GEN-LAST:event_jMenuItemEditCategoriesActionPerformed

    private void tasksTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tasksTblMouseClicked
        if(evt.getClickCount() == 2) {
            log.info("mouse doubleclicked: " + tasksTbl.getValueAt(tasksTbl.getSelectedRow(), 0).toString());
            Document d = taskEdit.getDocument();
            try {
                //d.insertString(taskEdit.getCaretPosition(), tasksTbl.getValueAt(tasksTbl.getSelectedRow(), 0).toString() + ": ", null);
                String taskString = tasksTbl.getValueAt(tasksTbl.getSelectedRow(), 0).toString();
                d.insertString(taskEdit.getCaretPosition(), taskString.startsWith("@") ? taskString : taskString + ": ", null);
            } catch (BadLocationException e) {
                log.warn("Task insert error", e);
            }
            taskEdit.requestFocus();
        }
    }//GEN-LAST:event_tasksTblMouseClicked

    private void jMenuItemReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReloadActionPerformed
        try {
            // TODO add your handling code here:
            ctrlCats.reloadCats();
            ctrlLogs.reInit();
        } catch (JTimeLogException ex) {
            log.error("Error while reloading categories", ex);
            logsViewTA.setText(ex.toString());
        }
        populateCats();
    }//GEN-LAST:event_jMenuItemReloadActionPerformed

    private void jTFReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFReportActionPerformed
        String out = GTLSReport.generateReport(jCBReportType.getSelectedItem().toString(), Arrays.asList((jTFReport.getText()).split(" +")));
        logsViewTA.setText(out);
        logsViewTA.setCaretPosition(0);
    }//GEN-LAST:event_jTFReportActionPerformed

    private void openFile(File f) {
        if (Desktop.isDesktopSupported() == false) {
            throw new RuntimeException("Desktop operation open file is not supported");
        }
        try {
            Desktop.getDesktop().open(f);
        } catch (IOException ex) {
            throw new RuntimeException("Opening file '%s' failed", ex);
        }
    }

    private void updateDisplaydDayLabel() {
        displayDayL.setText(ctrlLogs.getDisplayedDay().format(DateTimeFormatter.ofPattern(Configuration.getCfgString("DATEWITHDAYPATTERN"))));
    }

    private void setNewLogMsg() {
        String msg = taskEdit.getText().trim();
        if (msg.length() == 0) {
            // no msg, no need to add entry
            return;
        }

        log.debug("Adding new message to log");
        try {
            ctrlLogs.addMessage(msg);
            taskEdit.setText("");
        } catch (JTimeLogException e) {
            log.error("Invalid message to store", e);
            throw new RuntimeException("Invalid message to store", e);
        }
    }

    @Override
    public void entriesChanged(LocalDate day) {
        if (day == null || day.equals(ctrlLogs.getDisplayedDay())) {
            log.debug("Recreating content of text area");
            logsViewTA.setText(ctrlFormat.getCurrent().formatDayData(ctrlLogs.getDisplayedDayLogEntries()));
            
            
            /*
            Following code:
            - get DateTime of last entry of current day
            - if we have at least 1 entry for the day, than start time counter in background
            - if not, don't start counter
            - there is optimization - if we only switched formatting or displayed day, than don't remove and restart new counter, do nothing
            */
            LocalDateTime lastTimestamp = ctrlLogs.getDayLogEntries( ctrlLogs.getCurrentDay() ).getLastEntryTimestamp();
            if(timerSinceLastMsgTask != null) {
                if(lastTimestamp != null && !timerSinceLastMsgTask.lastEntry.equals(lastTimestamp)) {
                    timerSinceLastMsgTask.cancel(true);
                    timerSinceLastMsgTask = null;
                }
            }
            if(lastTimestamp != null && timerSinceLastMsgTask == null) {
                timerSinceLastMsgTask = new TimerSinceLastMsgTask(lastTimestamp);
                timerSinceLastMsgTask.execute();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JTimeLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JTimeLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JTimeLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JTimeLog.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JTimeLog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentTValL;
    private javax.swing.JLabel displayDayL;
    private javax.swing.JButton goDayNextB;
    private javax.swing.JButton goDayPrevB;
    private javax.swing.JButton goDayTodayB;
    private javax.swing.JComboBox<String> jCBReportType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemEditCategories;
    private javax.swing.JMenuItem jMenuItemEditTasks;
    private javax.swing.JMenuItem jMenuItemFormatDetail;
    private javax.swing.JMenuItem jMenuItemFormatDetailSorted;
    private javax.swing.JMenuItem jMenuItemFormatSummary;
    private javax.swing.JMenuItem jMenuItemNextDay;
    private javax.swing.JMenuItem jMenuItemNextWeek;
    private javax.swing.JMenuItem jMenuItemPreviousDay;
    private javax.swing.JMenuItem jMenuItemPreviousWeek;
    private javax.swing.JMenuItem jMenuItemReload;
    private javax.swing.JMenuItem jMenuItemToday;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jTFReport;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextArea logsViewTA;
    private javax.swing.JTextField taskEdit;
    private javax.swing.JTable tasksTbl;
    // End of variables declaration//GEN-END:variables

}
