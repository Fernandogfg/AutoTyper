package autoTyper;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
//TODO
/*
-Decidir entre multiplas hotkeys variáveis ou multiplas hotkeys fixas
-Transformar o tempo entre as teclas em uma variavel que vai poder ser alterada pelo usuário
-Adicionar a possibilidade de alterar o idioma da interface
-Formatar o codigo para inglês e evitar codigo bilingue ####
-Possibilitar que simbolos possam ser utilizados tanto como hotkeys como possam ser digitados
-Evitar que caso a hotkey seja pressinada durante a simulação ela acione novamente o script
-Adicionar logo
*/

public class AutoTyper implements ActionListener, NativeKeyListener {
	private JLabel label;
	private JFrame frame;
	private JPanel panel;
	private JTextField keyCamp;
	private String message;
	private JLabel messageLabel;
	private JTextField messageCamp;
	@SuppressWarnings("unused")
	private boolean listening = false;
	private String configuredKey;
	private int interval = 100;

	public AutoTyper() {

		// Configurando elementos
		frame = new JFrame();
		label = new JLabel("Digite a tecla que será usada para ativação.");
		messageLabel = new JLabel("Digite a mensagem que vai ser enviada");
		JButton button = new JButton("Configurar");
		keyCamp = new JTextField();
		keyCamp.setPreferredSize(new Dimension(10, 2));
		messageCamp = new JTextField();

		button.addActionListener(this);

		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
		panel.setLayout(new GridLayout(0, 1));

		// Adicionando elementos no painel.
		panel.add(label);
		panel.add(keyCamp);
		panel.add(messageLabel);
		panel.add(messageCamp);
		panel.add(button);

		// Configurando o Frame
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("AutoTyper");
		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		new AutoTyper();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		label.setText("Configurado");
		message = messageCamp.getText();
		if ((keyCamp.getText()).length() > 1 || (messageCamp.getText()).length() == 0) {
			JOptionPane.showMessageDialog(frame, "Preencha os campos corretamente!");
		} else {
			configuredKey = keyCamp.getText();
			message = messageCamp.getText();
			try {
				GlobalScreen.registerNativeHook();
				GlobalScreen.addNativeKeyListener(this);
				listening = true;
			} catch (NativeHookException ex) {
				System.err.println("Erro ao registrar o Native Hook.");
				ex.printStackTrace();
			}
		}

		System.out.println("A mensagem gravada foi: " + message);
	}
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		String teclaPressionada = NativeKeyEvent.getKeyText(e.getKeyCode());
		System.out.println("Tecla Pressionada: " + teclaPressionada);

		if (teclaPressionada.equalsIgnoreCase(configuredKey)) {
			try {
				Robot robot = new Robot();
				for (char c : message.toCharArray()) {
					int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
					if (keyCode != KeyEvent.VK_UNDEFINED) {
						robot.keyPress(keyCode);
						robot.keyRelease(keyCode);
					}
					Thread.sleep( (int)(interval * Math.random()));
					
				}
			} catch (AWTException | InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}
}
