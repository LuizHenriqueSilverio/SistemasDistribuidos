package udp.chat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
        setTitle("UDP Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel connectionPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Configuração"));
        
    	nameField = new JTextField();
    	localPortField = new JTextField();
    	remotePortField = new JTextField();
    	messageField = new JTextField();
    	chatArea = new JTextArea();
    	connectButton = new JButton("Conectar");
    	sendButton = new JButton("Enviar");

        connectionPanel.add(new JLabel("Nome:"));
        connectionPanel.add(nameField);
        nameField.setToolTipText("Seu nome será exibido nas mensagens");

        connectionPanel.add(new JLabel("Porta Local:"));
        connectionPanel.add(localPortField);
        localPortField.setToolTipText("Porta UDP para ouvir mensagens");

        connectionPanel.add(new JLabel("Porta Remota:"));
        connectionPanel.add(remotePortField);
        remotePortField.setToolTipText("Porta UDP do destinatário");
        remotePortField.addActionListener(e -> connect());

        connectButton = new JButton("Conectar");
        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.add(connectButton, BorderLayout.CENTER);
        connectionPanel.add(new JLabel());
        connectionPanel.add(btnWrapper);

        add(connectionPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Conversa"));
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

        getContentPane().setPreferredSize(new Dimension(640, 480));
        pack();
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
