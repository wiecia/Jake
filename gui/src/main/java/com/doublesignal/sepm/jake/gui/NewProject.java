package com.doublesignal.sepm.jake.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.ExistingProjectException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidDatabaseException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.services.exceptions.NonExistantDatabaseException;
import com.doublesignal.sepm.jake.fss.NotADirectoryException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;

/**
 * @author johannes, peter
 */
@SuppressWarnings("serial")
public class NewProject extends JDialog {
	ITranslationProvider translator = null;
	JakeGuiAccess jga = null;
	private static Logger log = Logger.getLogger(NewProject.class);

	public NewProject() {
		super();
		log.debug("NewProject dialog starts");
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource(
				"beans.xml"));
		translator = (ITranslationProvider) factory
				.getBean("translationProvider");
		log.debug("NewProject:initComponents");
		initComponents();
		log.debug("NewProject:setVisible");
		setVisible(true);
	}

	private void folderSelectActionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser(folderTextField.getText());
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnCode = fileChooser.showOpenDialog(null);
		if (returnCode == JFileChooser.APPROVE_OPTION) {
			String rootPath = fileChooser.getSelectedFile().getAbsolutePath();
			folderTextField.setText(rootPath);

			projectNameTextField.setEditable(true);
			okButton.setEnabled(false);
			folderTextField.setBackground(Color.WHITE);
			jga = null;
			try {
				jga = JakeGuiAccess.openProjectByRootpath(rootPath);
				okButton.setEnabled(true);
				folderTextField.setBackground(Color.GREEN);

				projectNameTextField.setEditable(false);
				projectNameTextField.setText(jga.getProject().getName());

				okButton.setText(translator.get("NewProjectDialogOpenProject"));
				okButton.setEnabled(true);
			} catch (NonExistantDatabaseException e) {
				okButton.setText(translator
						.get("NewProjectDialogCreateProject"));
				projectNameTextField.setEditable(true);
				projectNameTextField.setText(new File(rootPath).getName());
				okButton.setEnabled(true);
			} catch (InvalidDatabaseException e) {
				folderTextField.setBackground(Color.RED);
				UserDialogHelper.error(this, "Invalid Database");
			} catch (InvalidRootPathException e) {
				UserDialogHelper.error(this, "Invalid root path");
				folderTextField.setBackground(Color.RED);
			}
		}
	}

	private void okButtonActionPerformed(ActionEvent event) {
		if (jga != null) {
			log.info("starting main window with opened database ...");
			new JakeGui(jga);
			setVisible(false);
		} else {
			try {
				log.info("creating database ...");
				jga = JakeGuiAccess.createNewProjectByRootpath(folderTextField
						.getText(), projectNameTextField.getText());
				setVisible(false);
				log.info("created Database, starting main window");
				new JakeGui(jga);
			} catch (ExistingProjectException e) {
				log.error("Project already exists");
				UserDialogHelper.error(this, translator
						.get("Project already exists"));
			} catch (InvalidDatabaseException e) {
				log.error("Invalid Database");
				UserDialogHelper
						.error(this, translator.get("Invalid Database"));
			} catch (NotADirectoryException e) {
				log.error("Invalid Project Directory");
				UserDialogHelper.error(this, translator
						.get("Invalid Project Directory"));
			} catch (InvalidRootPathException e) {
				log.error("Invalid Project Directory");
				UserDialogHelper.error(this, translator
						.get("Invalid Project Directory"));
			}
		}
	}

	private void cancelButtonActionPerformed(ActionEvent event) {
		System.exit(0);
	}

	private void initComponents() {
		headerPanel = new JPanel();
		jakeIconLabel = new JLabel();
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		infoLabel = new JLabel();
		selectFolderLabel = new JLabel();
		folderLabel = new JLabel();
		folderTextField = new JTextField();
		folderSelectButton = new JButton();
		nameProjectLabel = new JLabel();
		projectNameLabel = new JLabel();
		projectNameTextField = new JTextField();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		// ======== this ========
		setTitle(translator.get("NewProjectDialogTitle"));
		setResizable(false);
		setModal(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		headerPanel.setBackground(Color.white);
		headerPanel.setLayout(new FlowLayout());

		try {
			jakeIconLabel.setIcon(new ImageIcon(new ClassPathResource(
					"jake.gif").getURL()));
		} catch (IOException e1) {
			log.warn("image icon not found.");
		}
		jakeIconLabel.setBackground(Color.white);
		jakeIconLabel.setText("Welcome to Jake!");
		jakeIconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		jakeIconLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		jakeIconLabel.setAlignmentX(5.0F);
		jakeIconLabel.setAlignmentY(5.5F);
		headerPanel.add(jakeIconLabel);

		contentPane.add(headerPanel, BorderLayout.NORTH);

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setLayout(new TableLayout(
						new double[][] {
								{ TableLayout.PREFERRED, 246, 64 },
								{ TableLayout.PREFERRED, TableLayout.PREFERRED,
										TableLayout.PREFERRED,
										TableLayout.PREFERRED,
										TableLayout.PREFERRED,
										TableLayout.PREFERRED } }));
				((TableLayout) contentPanel.getLayout()).setHGap(2);
				((TableLayout) contentPanel.getLayout()).setVGap(15);

				// ---- infoLabel ----
				infoLabel.setText(translator.get("NewProjectDialogInfo"));
				contentPanel.add(infoLabel, new TableLayoutConstraints(0, 0, 2,
						0, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- selectFolderLabel ----
				selectFolderLabel.setText(translator
						.get("NewProjectDialogSelectFolder"));
				contentPanel.add(selectFolderLabel, new TableLayoutConstraints(
						0, 1, 1, 1, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- folderLabel ----
				folderLabel.setText(translator.get("Folder"));
				folderLabel.setIcon(UIManager
						.getIcon("FileChooser.newFolderIcon"));
				contentPanel.add(folderLabel, new TableLayoutConstraints(0, 2,
						0, 2, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				folderTextField.setEditable(false);
				contentPanel.add(folderTextField, new TableLayoutConstraints(1,
						2, 1, 2, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- folderSelectButton ----
				folderSelectButton.setText("...");
				folderSelectButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						folderSelectActionPerformed(event);
					}
				});
				contentPanel.add(folderSelectButton,
						new TableLayoutConstraints(2, 2, 2, 2,
								TableLayoutConstraints.FULL,
								TableLayoutConstraints.FULL));

				// ---- nameProjectLabel ----
				nameProjectLabel.setText(translator
						.get("NewProjectDialogNameProject"));
				contentPanel.add(nameProjectLabel, new TableLayoutConstraints(
						0, 3, 2, 3, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- projectNameLabel ----
				projectNameLabel.setText(translator
						.get("NewProjectDialogProjectName"));
				contentPanel.add(projectNameLabel, new TableLayoutConstraints(
						0, 4, 0, 4, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				contentPanel.add(projectNameTextField,
						new TableLayoutConstraints(1, 4, 2, 4,
								TableLayoutConstraints.FULL,
								TableLayoutConstraints.FULL));
			}
			dialogPane.add(contentPanel, BorderLayout.NORTH);

			// ======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {
						0, 85, 80 };
				((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {
						1.0, 0.0, 0.0 };

				// ---- cancelButton ----
				cancelButton.setText(translator.get("ButtonClose"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						cancelButtonActionPerformed(event);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

				// ---- okButton ----
				okButton.setText(translator
						.get("NewProjectDialogCreateOpenProject"));
				okButton.setEnabled(false);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						okButtonActionPerformed(event);
					}
				});
				buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0,
						0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel headerPanel;
	private JLabel jakeIconLabel;
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel infoLabel;
	private JLabel selectFolderLabel;
	private JLabel folderLabel;
	private JTextField folderTextField;
	private JButton folderSelectButton;
	private JLabel nameProjectLabel;
	private JLabel projectNameLabel;
	private JTextField projectNameTextField;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
