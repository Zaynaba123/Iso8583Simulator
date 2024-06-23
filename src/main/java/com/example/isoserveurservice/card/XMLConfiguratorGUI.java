package com.example.isoserveurservice.card;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLConfiguratorGUI {
    private JFrame frame;
    private JTable requestTable;
    private JTable responseTable;
    private DefaultTableModel requestTableModel;
    private DefaultTableModel responseTableModel;
    private Document xmlDocument;
    private String xmlFilePath;

    public XMLConfiguratorGUI(String xmlFilePath) {
        this.xmlFilePath = "CardCfg/card.xml";
        initialize();
        loadXML();
        populateTables();
    }

    private void initialize() {
        frame = new JFrame("XML Configurator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        requestTableModel = new DefaultTableModel(new Object[]{"Case", "Field ID", "Field Name", "Field Wording", "Field Value"}, 0);
        requestTable = new JTable(requestTableModel);
        responseTableModel = new DefaultTableModel(new Object[]{"Case", "Field ID", "Field Name", "Field Value"}, 0);
        responseTable = new JTable(responseTableModel);

        // Custom cell renderer to merge case name cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0) {
                    if (row > 0 && value.equals(table.getValueAt(row - 1, column))) {
                        setText("");
                    } else {
                        setText(value.toString());
                    }
                }
                return cell;
            }
        };
        requestTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        responseTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);

        JPanel requestPanel = new JPanel(new BorderLayout());
        requestPanel.add(new JLabel("Request Table"), BorderLayout.NORTH);
        requestPanel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.add(new JLabel("Response Table"), BorderLayout.NORTH);
        responsePanel.add(new JScrollPane(responseTable), BorderLayout.CENTER);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1));
        tablesPanel.add(requestPanel);
        tablesPanel.add(responsePanel);

        frame.add(tablesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Field");
        JButton removeButton = new JButton("Remove Field");
        JButton saveButton = new JButton("Save XML");
        JButton editButton = new JButton("Edit Field");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addField();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeField();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editField();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveXML();
            }
        });

        requestTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateXMLFromTable(e.getFirstRow(), e.getColumn(), requestTableModel, "request");
                }
            }
        });

        responseTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateXMLFromTable(e.getFirstRow(), e.getColumn(), responseTableModel, "response");
                }
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private void loadXML() {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            xmlDocument = saxBuilder.build(new File(xmlFilePath));
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    private void populateTables() {
        Element rootElement = xmlDocument.getRootElement();
        List<Element> cases = rootElement.getChildren("case");

        for (Element caseElement : cases) {
            String caseName = caseElement.getAttributeValue("name");

            Element requestElement = caseElement.getChild("request");
            List<Element> requestFields = requestElement.getChildren("field");
            for (Element field : requestFields) {
                requestTableModel.addRow(new Object[]{
                        caseName,
                        field.getAttributeValue("id"),
                        field.getAttributeValue("NAME"),
                        field.getAttributeValue("WORDING"),
                        field.getAttributeValue("value")
                });
            }

            Element responseElement = caseElement.getChild("response");
            List<Element> responseFields = responseElement.getChildren("field");
            for (Element field : responseFields) {
                responseTableModel.addRow(new Object[]{
                        caseName,
                        field.getAttributeValue("id"),
                        field.getAttributeValue("NAME"),
                        field.getAttributeValue("value")
                });
            }
        }
    }

    private void addField() {
        JTextField caseField = new JTextField();
        JTextField fieldIdField = new JTextField();
        JTextField fieldNameField = new JTextField();
        JTextField fieldWordingField = new JTextField();
        JTextField fieldValueField = new JTextField();
        JTextField responseFieldId = new JTextField();
        JTextField responseFieldName = new JTextField();
        JTextField responseFieldValue = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(9, 2));
        inputPanel.add(new JLabel("Case Name:"));
        inputPanel.add(caseField);
        inputPanel.add(new JLabel("Request Field ID:"));
        inputPanel.add(fieldIdField);
        inputPanel.add(new JLabel("Request Field Name:"));
        inputPanel.add(fieldNameField);
        inputPanel.add(new JLabel("Request Field Wording:"));
        inputPanel.add(fieldWordingField);
        inputPanel.add(new JLabel("Request Field Value:"));
        inputPanel.add(fieldValueField);
        inputPanel.add(new JLabel("Response Field ID:"));
        inputPanel.add(responseFieldId);
        inputPanel.add(new JLabel("Response Field Name:"));
        inputPanel.add(responseFieldName);
        inputPanel.add(new JLabel("Response Field Value:"));
        inputPanel.add(responseFieldValue);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Add New Field", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String caseName = caseField.getText();
            String fieldId = fieldIdField.getText();
            String fieldName = fieldNameField.getText();
            String fieldWording = fieldWordingField.getText();
            String fieldValue = fieldValueField.getText();
            String responseId = responseFieldId.getText();
            String responseName = responseFieldName.getText();
            String responseValue = responseFieldValue.getText();

            boolean caseFound = false;
            int requestInsertIndex = -1;

            for (int i = 0; i < requestTableModel.getRowCount(); i++) {
                if (requestTableModel.getValueAt(i, 0).equals(caseName)) {
                    requestInsertIndex = i;
                    caseFound = true;
                }
            }

            if (caseFound && requestInsertIndex != -1) {
                // Insert at the end of the existing case rows
                while (requestInsertIndex < requestTableModel.getRowCount() &&
                        requestTableModel.getValueAt(requestInsertIndex, 0).equals(caseName)) {
                    requestInsertIndex++;
                }
                requestTableModel.insertRow(requestInsertIndex, new Object[]{caseName, fieldId, fieldName, fieldWording, fieldValue});
            } else {
                requestTableModel.addRow(new Object[]{caseName, fieldId, fieldName, fieldWording, fieldValue});
                responseTableModel.addRow(new Object[]{caseName, responseId, responseName, responseValue});
            }
        }
    }

    private void removeField() {
        String[] options = {"Request", "Response"};
        int choice = JOptionPane.showOptionDialog(frame, "Select where to remove the field from:", "Remove Field",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        int selectedRow = (choice == 0) ? requestTable.getSelectedRow() : responseTable.getSelectedRow();
        if (selectedRow != -1) {
            if (choice == 0) {
                requestTableModel.removeRow(selectedRow);
            } else {
                responseTableModel.removeRow(selectedRow);
            }
        }
    }

    private void editField() {
        String[] options = {"Request", "Response"};
        int choice = JOptionPane.showOptionDialog(frame, "Select where to edit the field:", "Edit Field",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        int selectedRow = (choice == 0) ? requestTable.getSelectedRow() : responseTable.getSelectedRow();
        if (selectedRow != -1) {
            JTextField caseField = new JTextField((String) ((choice == 0) ? requestTableModel.getValueAt(selectedRow, 0) : responseTableModel.getValueAt(selectedRow, 0)));
            JTextField fieldIdField = new JTextField((String) ((choice == 0) ? requestTableModel.getValueAt(selectedRow, 1) : responseTableModel.getValueAt(selectedRow, 1)));
            JTextField fieldNameField = new JTextField((String) ((choice == 0) ? requestTableModel.getValueAt(selectedRow, 2) : responseTableModel.getValueAt(selectedRow, 2)));
            JTextField fieldWordingField = new JTextField((String) ((choice == 0) ? requestTableModel.getValueAt(selectedRow, 3) : responseTableModel.getValueAt(selectedRow, 2)));
            JTextField fieldValueField = new JTextField((String) ((choice == 0) ? requestTableModel.getValueAt(selectedRow, 4) : responseTableModel.getValueAt(selectedRow, 3)));

            JPanel inputPanel = new JPanel(new GridLayout(5, 2));
            inputPanel.add(new JLabel("Case Name:"));
            inputPanel.add(caseField);
            inputPanel.add(new JLabel("Field ID:"));
            inputPanel.add(fieldIdField);
            inputPanel.add(new JLabel("Field Name:"));
            inputPanel.add(fieldNameField);
            inputPanel.add(new JLabel("Field Wording:"));
            inputPanel.add(fieldWordingField);
            inputPanel.add(new JLabel("Field Value:"));
            inputPanel.add(fieldValueField);

            int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Edit Field", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String caseName = caseField.getText();
                String fieldId = fieldIdField.getText();
                String fieldName = fieldNameField.getText();
                String fieldWording = fieldWordingField.getText();
                String fieldValue = fieldValueField.getText();

                if (choice == 0) {
                    requestTableModel.setValueAt(caseName, selectedRow, 0);
                    requestTableModel.setValueAt(fieldId, selectedRow, 1);
                    requestTableModel.setValueAt(fieldName, selectedRow, 2);
                    requestTableModel.setValueAt(fieldWording, selectedRow, 3);
                    requestTableModel.setValueAt(fieldValue, selectedRow, 4);
                } else {
                    responseTableModel.setValueAt(caseName, selectedRow, 0);
                    responseTableModel.setValueAt(fieldId, selectedRow, 1);
                    responseTableModel.setValueAt(fieldName, selectedRow, 2);
                    responseTableModel.setValueAt(fieldValue, selectedRow, 3);
                }
            }
        }
    }

    private void saveXML() {
        try {
            Element rootElement = xmlDocument.getRootElement();
            rootElement.removeContent();

            Map<String, Element> caseElements = new HashMap<>();

            for (int i = 0; i < requestTableModel.getRowCount(); i++) {
                String caseName = (String) requestTableModel.getValueAt(i, 0);
                String fieldId = (String) requestTableModel.getValueAt(i, 1);
                String fieldName = (String) requestTableModel.getValueAt(i, 2);
                String fieldWording = (String) requestTableModel.getValueAt(i, 3);
                String fieldValue = (String) requestTableModel.getValueAt(i, 4);

                Element caseElement = caseElements.computeIfAbsent(caseName, k -> {
                    Element newCaseElement = new Element("case");
                    newCaseElement.setAttribute("name", caseName);
                    newCaseElement.addContent(new Element("request"));
                    newCaseElement.addContent(new Element("response"));
                    rootElement.addContent(newCaseElement);
                    return newCaseElement;
                });

                Element requestElement = caseElement.getChild("request");
                Element fieldElement = new Element("field");
                fieldElement.setAttribute("id", fieldId);
                fieldElement.setAttribute("NAME", fieldName);
                fieldElement.setAttribute("WORDING", fieldWording);
                fieldElement.setAttribute("value", fieldValue);
                requestElement.addContent(fieldElement);
            }

            for (int i = 0; i < responseTableModel.getRowCount(); i++) {
                String caseName = (String) responseTableModel.getValueAt(i, 0);
                String fieldId = (String) responseTableModel.getValueAt(i, 1);
                String fieldName = (String) responseTableModel.getValueAt(i, 2);
                String fieldValue = (String) responseTableModel.getValueAt(i, 3);

                Element caseElement = caseElements.computeIfAbsent(caseName, k -> {
                    Element newCaseElement = new Element("case");
                    newCaseElement.setAttribute("name", caseName);
                    newCaseElement.addContent(new Element("request"));
                    newCaseElement.addContent(new Element("response"));
                    rootElement.addContent(newCaseElement);
                    return newCaseElement;
                });

                Element responseElement = caseElement.getChild("response");
                Element fieldElement = new Element("field");
                fieldElement.setAttribute("id", fieldId);
                fieldElement.setAttribute("NAME", fieldName);
                fieldElement.setAttribute("value", fieldValue);
                responseElement.addContent(fieldElement);
            }

            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(xmlDocument, new FileWriter(xmlFilePath));

            JOptionPane.showMessageDialog(frame, "XML file saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving XML file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateXMLFromTable(int row, int column, DefaultTableModel model, String type) {
        String caseName = (String) model.getValueAt(row, 0);
        String fieldId = (String) model.getValueAt(row, 1);

        Element rootElement = xmlDocument.getRootElement();
        List<Element> cases = rootElement.getChildren("case");

        for (Element caseElement : cases) {
            if (caseElement.getAttributeValue("name").equals(caseName)) {
                Element targetElement = caseElement.getChild(type);
                List<Element> fields = targetElement.getChildren("field");
                for (Element field : fields) {
                    if (field.getAttributeValue("id").equals(fieldId)) {
                        if (type.equals("request")) {
                            switch (column) {
                                case 2:
                                    field.setAttribute("NAME", (String) model.getValueAt(row, column));
                                    break;
                                case 3:
                                    field.setAttribute("WORDING", (String) model.getValueAt(row, column));
                                    break;
                                case 4:
                                    field.setAttribute("value", (String) model.getValueAt(row, column));
                                    break;
                            }
                        } else {
                            switch (column) {
                                case 2:
                                    field.setAttribute("NAME", (String) model.getValueAt(row, column));
                                    break;
                                case 3:
                                    field.setAttribute("value", (String) model.getValueAt(row, column));
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new XMLConfiguratorGUI("CardCfg/card.xml"));
    }
}
