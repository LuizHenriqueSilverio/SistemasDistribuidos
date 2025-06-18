package sd.chat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;

import sd.chat.ChatException;
import sd.chat.ChatFactory;
import sd.chat.MessageContainer;
import sd.chat.Receiver;
import sd.chat.Sender;

public class ChatWindow extends JFrame implements MessageContainer {

	private static final long serialVersionUID = 1L;
	private JTextField localPortField, remotePortField, messageField, nameField, remoteIpField;
	private JRadioButton tcpRadioButton, udpRadioButton;
	private ButtonGroup protocolGroup;
	private JTextArea chatArea;
	private JButton connectButton, sendButton, disconnectButton;
	private Sender sender;
	private Receiver receiver;
	private String userName;

	public ChatWindow() {
		setTitle("TCP/UDP Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		JPanel connectionPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		connectionPanel.setBorder(BorderFactory.createTitledBorder("Configuração"));

		nameField = new JTextField();
		localPortField = new JTextField();
		remoteIpField = new JTextField("localhost");
		remotePortField = new JTextField();
		connectButton = new JButton("Conectar");
		disconnectButton = new JButton("Desconectar");
		disconnectButton.setEnabled(false);

		connectionPanel.add(new JLabel("Nome:"));
		connectionPanel.add(nameField);
		connectionPanel.add(new JLabel("Porta Local:"));
		connectionPanel.add(localPortField);
		connectionPanel.add(new JLabel("IP Remoto:"));
		connectionPanel.add(remoteIpField);
		connectionPanel.add(new JLabel("Porta Remota:"));
		connectionPanel.add(remotePortField);

		tcpRadioButton = new JRadioButton("TCP", true);
		udpRadioButton = new JRadioButton("UDP");
		protocolGroup = new ButtonGroup();
		protocolGroup.add(tcpRadioButton);
		protocolGroup.add(udpRadioButton);
		JPanel protocolPanel = new JPanel();
		protocolPanel.add(tcpRadioButton);
		protocolPanel.add(udpRadioButton);
		connectionPanel.add(new JLabel("Protocolo:"));
		connectionPanel.add(protocolPanel);

		connectionPanel.add(connectButton);
		connectionPanel.add(disconnectButton);

		add(connectionPanel, BorderLayout.NORTH);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Conversa"));
		add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
		messageField = new JTextField();
		sendButton = new JButton("Enviar");
		bottomPanel.add(messageField, BorderLayout.CENTER);
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
		disconnectButton.addActionListener(e -> disconnect());
		sendButton.addActionListener(e -> sendMessage());
		messageField.addActionListener(e -> sendMessage());
	}

	private void setConnectionFieldsEnabled(boolean enabled) {
		nameField.setEditable(enabled);
		localPortField.setEditable(enabled);
		remoteIpField.setEditable(enabled);
		remotePortField.setEditable(enabled);
		tcpRadioButton.setEnabled(enabled);
		udpRadioButton.setEnabled(enabled);
		connectButton.setEnabled(enabled);
		disconnectButton.setEnabled(!enabled);
	}

	private void connect() {
		try {
			userName = nameField.getText().trim();
			int localPort = Integer.parseInt(localPortField.getText().trim());
			String remoteIp = remoteIpField.getText().trim();
			int serverPort = Integer.parseInt(remotePortField.getText().trim());
			boolean isTcp = tcpRadioButton.isSelected();

			if (userName.isEmpty() || remoteIp.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Preencha todos os campos de configuração.", "Erro",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			setConnectionFieldsEnabled(false);
			chatArea.setText("");
			if (isTcp) {
				chatArea.append("Aguardando conexão do outro usuário...\nIsto pode levar um momento.\n");
			} else {
				chatArea.append("Iniciando chat UDP...\n");
			}

			new SwingWorker<Sender, Void>() {
				@Override
				protected Sender doInBackground() throws Exception {
					return ChatFactory.build(isTcp, remoteIp, serverPort, localPort, ChatWindow.this);
				}

				@Override
				protected void done() {
					try {
						sender = get();
						receiver = ChatFactory.getLastReceiver(); // Pega o receiver criado

						messageField.setEnabled(true);
						sendButton.setEnabled(true);
						chatArea.append("Conectado com sucesso em modo " + (isTcp ? "TCP" : "UDP") + "!\n");
					} catch (Exception ex) {
						chatArea.append("Falha na conexão: " + ex.getCause().getMessage() + "\n");
						JOptionPane.showMessageDialog(ChatWindow.this,
								"Erro ao conectar: " + ex.getCause().getMessage(), "Erro de Conexão",
								JOptionPane.ERROR_MESSAGE);
						setConnectionFieldsEnabled(true);
					}
				}
			}.execute();

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Número de porta inválido!", "Erro de Formato",
					JOptionPane.ERROR_MESSAGE);
			setConnectionFieldsEnabled(true);
		}
	}

	private void disconnect() {
		if (sender != null)
			sender.close();
		if (receiver != null)
			receiver.close();

		sender = null;
		receiver = null;

		setConnectionFieldsEnabled(true);
		messageField.setEnabled(false);
		sendButton.setEnabled(false);
		chatArea.append("\nDesconectado.\n");
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
				JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro de Envio",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void newMessage(String message) {
		if (message == null || message.trim().isEmpty())
			return;

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