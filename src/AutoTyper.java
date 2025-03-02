package autoTyper;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
	private JFrame frame;

	private JPanel titlePanel;
	private JLabel titleLabel;

	private JPanel keyPanel;
	private JLabel keyLabel;
	private JTextField keyTextField;

	private JPanel messagePanel;
	private JLabel messageLabel;
	private JTextField messageTextField;
	private String message;

	private JPanel optPanel;
	private JButton helpBtn;
	private JButton doneBtn;

	private boolean typing = false;
	private String configuredKey;
	private int interval = 100;

	public AutoTyper() {

		// Configurando Frame
		frame = new JFrame();
		frame.setLayout(new GridLayout(4, 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("AutoTyper");

		// Configurando Titulo
		titlePanel = new JPanel();
		titleLabel = new JLabel("AutoTyper!");
		titleLabel.setFont(new Font("Serif", Font.BOLD, 15));
		titlePanel.add(titleLabel);

		// Configurando painel principal
		// painel de Adição de chave
		keyPanel = new JPanel(new GridLayout(1, 2));
		keyLabel = new JLabel("Key: ");
		keyTextField = new JTextField(1);
		keyPanel.add(keyLabel);
		keyPanel.add(keyTextField);

		// painel de adição da mensagem
		messagePanel = new JPanel(new GridLayout(1, 2));
		messageLabel = new JLabel("Mensagem: ");
		messageTextField = new JTextField();
		messagePanel.add(messageLabel);
		messagePanel.add(messageTextField);

		// Configurando painel de opções
		optPanel = new JPanel(new GridLayout(1, 2));
		helpBtn = new JButton("ajuda");
		doneBtn = new JButton("feito");
		optPanel.add(helpBtn);
		optPanel.add(doneBtn);

		// Configurando botão de "pronto"
		doneBtn.addActionListener(this);

		// Adicionando elementos ao Frame
		frame.add(titlePanel);
		frame.add(keyPanel);
		frame.add(messagePanel);
		frame.add(optPanel);
		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		new AutoTyper();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == doneBtn
				&& ((keyTextField.getText()).length() == 1 && (messageTextField.getText()).length() > 0)) {
			configuredKey = keyTextField.getText();
			message = messageTextField.getText();
			try {
				GlobalScreen.registerNativeHook();
				GlobalScreen.addNativeKeyListener(this);

			} catch (NativeHookException ex) {
				System.err.println("Erro ao registrar o Native Hook.");
				ex.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(frame, "Preencha os campos corretamente!");
		}
		System.out.println("A mensagem gravada foi: " + message);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		String pressedKey = NativeKeyEvent.getKeyText(e.getKeyCode());
		System.out.println("Tecla Pressionada: " + pressedKey);

		if (pressedKey.equalsIgnoreCase(configuredKey) && !typing) {
			typing = true;
			new Thread(() -> {
				try {
					Robot robot = new Robot();
					for (char c : message.toCharArray()) {
						int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
						if (keyCode != KeyEvent.VK_UNDEFINED) {
							robot.keyPress(keyCode);
							robot.keyRelease(keyCode);
						}
						Thread.sleep((int) (interval * Math.random()));
					}
				} catch (AWTException | InterruptedException ex) {
					ex.printStackTrace();
				} finally {
					typing = false;
				}
			}).start();
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}
}
