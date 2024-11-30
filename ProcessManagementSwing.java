package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProcessManagementSwing extends JFrame {
	private static final long serialVersionUID = 1L;

	
	static class Process {
        int pid, arrivalTime, burstTime, startTime, completionTime, turnaroundTime, waitingTime;

        Process(int pid, int arrivalTime, int burstTime) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
        }
    }

    private DefaultTableModel tableModel;
    private List<Process> processes;
    private JTextField arrivalField;
    private JTextField burstField;
    private int processCounter = 1;

    public ProcessManagementSwing() {
        processes = new ArrayList<>();

        setTitle("Process Scheduling");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Process"));
        JLabel arrivalLabel = new JLabel("Arrival Time:");
        JLabel burstLabel = new JLabel("Burst Time:");
        arrivalField = new JTextField();
        burstField = new JTextField();
        JButton addButton = new JButton("Add Process");
        inputPanel.add(arrivalLabel);
        inputPanel.add(arrivalField);
        inputPanel.add(burstLabel);
        inputPanel.add(burstField);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        // Table Panel
        String[] columnNames = {"PID", "Arrival Time", "Burst Time", "Start Time", "Completion Time", "Turnaround Time", "Waiting Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Action Panel
        JPanel actionPanel = new JPanel();
        JButton fcfsButton = new JButton("Run FCFS");
        JButton sjfButton = new JButton("Run SJF");
        actionPanel.add(fcfsButton);
        actionPanel.add(sjfButton);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Add Process Button Action
        addButton.addActionListener(e -> addProcess());

        // FCFS Button Action
        fcfsButton.addActionListener(e -> {
            runFCFS();
            updateTable();
        });

        // SJF Button Action
        sjfButton.addActionListener(e -> {
            runSJF();
            updateTable();
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addProcess() {
        try {
            int arrivalTime = Integer.parseInt(arrivalField.getText());
            int burstTime = Integer.parseInt(burstField.getText());
            processes.add(new Process(processCounter++, arrivalTime, burstTime));
            arrivalField.setText("");
            burstField.setText("");
            updateTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Arrival and Burst Time.");
        }
    }

    private void runFCFS() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;

        for (Process process : processes) {
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;
            }
            process.startTime = currentTime;
            process.completionTime = currentTime + process.burstTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.burstTime;
            currentTime += process.burstTime;
        }
    }

    private void runSJF() {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0, completed = 0, n = processes.size();
        boolean[] isCompleted = new boolean[n];

        while (completed != n) {
            int idx = -1, minBurst = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process process = processes.get(i);
                if (!isCompleted[i] && process.arrivalTime <= currentTime && process.burstTime < minBurst) {
                    minBurst = process.burstTime;
                    idx = i;
                }
            }

            if (idx != -1) {
                Process process = processes.get(idx);
                process.startTime = currentTime;
                process.completionTime = currentTime + process.burstTime;
                process.turnaroundTime = process.completionTime - process.arrivalTime;
                process.waitingTime = process.turnaroundTime - process.burstTime;

                isCompleted[idx] = true;
                completed++;
                currentTime += process.burstTime;
            } else {
                currentTime++;
            }
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Process process : processes) {
            tableModel.addRow(new Object[]{
                    process.pid, process.arrivalTime, process.burstTime,
                    process.startTime, process.completionTime,
                    process.turnaroundTime, process.waitingTime
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProcessManagementSwing :: new);
    }
}
