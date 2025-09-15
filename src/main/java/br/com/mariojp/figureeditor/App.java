package br.com.mariojp.figureeditor;

import javax.swing.*;
import java.awt.*;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}

			JFrame frame = new JFrame("Figure Editor — Clique para inserir figuras");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			DrawingPanel panel = new DrawingPanel();

			frame.setLayout(new BorderLayout());
			frame.add(panel, BorderLayout.CENTER);

			JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton colorButton = new JButton("Cor");
			topPanel.add(colorButton);
			colorButton.addActionListener(e -> {
				Color chosen = JColorChooser.showDialog(frame, "Escolhe uma cor linda", panel.getCurrentColor());
				if (chosen != null) {
					panel.setCurrentColor(chosen);
				}
			});
			frame.add(topPanel, BorderLayout.NORTH);

			JMenuBar menuBar = new JMenuBar();
			JMenu fileMenu = new JMenu("Arquivo");

			JMenuItem exportPNG = new JMenuItem("Exportar para PNG");
			exportPNG.addActionListener(e -> exportarImagem("png", panel, frame));

			JMenuItem exportSVG = new JMenuItem("Exportar para SVG");
			exportSVG.addActionListener(e -> exportarImagem("svg", panel, frame));

			fileMenu.add(exportPNG);
			fileMenu.add(exportSVG);
			menuBar.add(fileMenu);
			frame.setJMenuBar(menuBar);
			
			frame.setSize(900, 600);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

	private static void exportarImagem(String tipo, DrawingPanel panel, JFrame frame) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Salvar como " + tipo.toUpperCase());

		int resultado = chooser.showSaveDialog(frame);
		if (resultado == JFileChooser.APPROVE_OPTION) {
			java.io.File arquivo = chooser.getSelectedFile();
			String caminho = arquivo.getAbsolutePath();

			if (!caminho.toLowerCase().endsWith("." + tipo)) {
				caminho += "." + tipo;
			}

			try {
				if (tipo.equals("png")) {
					panel.exportAsPNG(caminho);
				} else if (tipo.equals("svg")) {
					panel.exportAsSVG(caminho);
				}
				JOptionPane.showMessageDialog(frame, "Exportado com sucesso!");
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Erro ao exportar: " + ex.getMessage());
			}
		}
	}
}