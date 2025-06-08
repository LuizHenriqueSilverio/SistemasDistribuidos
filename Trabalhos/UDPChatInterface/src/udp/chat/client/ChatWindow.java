package udp.chat.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import udp.chat.ChatException;
import udp.chat.ChatFactory;
import udp.chat.MessageContainer;
import udp.chat.Sender;

public class ChatWindow extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;
    private JTextField localPortField, remotePortField, messageField, nameField;
    private JTextArea chatArea;
    private JButton connectButton, sendButton;
    private Sender sender;
    private String userName;

    public ChatWindow() {
        setTitle("SD Chat");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel connectionPanel = new JPanel(new java.awt.GridLayout(2, 4, 5, 5));
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        connectionPanel.add(new JLabel("Nome:"));
        nameField = new JTextField();
        connectionPanel.add(nameField);

        connectionPanel.add(new JLabel("Porta Local:"));
        localPortField = new JTextField();
        connectionPanel.add(localPortField);

        connectionPanel.add(new JLabel("Porta Remota:"));
        remotePortField = new JTextField();
        connectionPanel.add(remotePortField);

        connectionPanel.add(new JPanel());

        connectButton = new JButton("Conectar");
        connectionPanel.add(connectButton);

        add(connectionPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);
        sendButton = new JButton("Enviar");
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        setupActionListeners();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupActionListeners() {
        connectButton.addActionListener(e -> connect());
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }
    
    private void connect() {
        try {
            userName = nameField.getText().trim();
            int localPort = Integer.parseInt(localPortField.getText().trim());
            int serverPort = Integer.parseInt(remotePortField.getText().trim());

            if (userName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha o campo de nome.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.sender = ChatFactory.build("localhost", serverPort, localPort, this);

            nameField.setEditable(false);
            localPortField.setEditable(false);
            remotePortField.setEditable(false);
            connectButton.setEnabled(false);
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            chatArea.append("Conectado com sucesso!\n");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Número de porta inválido!", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + ex.getCause().getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty() && sender != null) {
            try {
                String messageToSend = String.format("%s%s%s", messageText, MessageContainer.FROM, userName);
                sender.send(messageToSend);
                
                chatArea.append(String.format("%s> %s\n", userName, messageText));
                messageField.setText("");
            } catch (ChatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro de Envio", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void newMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String[] messageParts = message.split(MessageContainer.FROM);
        String content = messageParts[0];
        String from = (messageParts.length > 1) ? messageParts[1].trim() : "Anônimo";

        SwingUtilities.invokeLater(() -> {
            chatArea.append(String.format("%s> %s\n", from, content));
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatWindow::new);
    }
}
