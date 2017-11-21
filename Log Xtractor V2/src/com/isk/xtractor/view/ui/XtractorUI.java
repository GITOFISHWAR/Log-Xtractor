package com.isk.xtractor.view.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.isk.log.core.applicationservice.LMSApplicationService;
import com.isk.log.core.dto.ConnectionDTO;
import com.isk.xtractor.model.entity.Environment;
import com.jcraft.jsch.Session;

public class XtractorUI extends JFrame {

	HashMap<String, Environment> allEnvdetails = null;
	String tmpFileNameStr1 = null;
	final JFrame jFrame = new JFrame("Log Xtractor V2 WP26");
	final JFileChooser fileChooser = new JFileChooser();

	JButton jButtonLoadConfig = new JButton();
	JLabel jLabelSuccess = new JLabel();
	JLabel jLabelError = new JLabel();

	JButton jButtonExportLogs = new JButton();

	private static final long serialVersionUID = 1L;

	public XtractorUI() {

		jFrame.setSize(600, 400);
		jFrame.setLayout(null);
		jFrame.setBounds(400, 150, 400, 200);
		jFrame.setLayout(new BorderLayout());
		jFrame.setContentPane(new JLabel(new ImageIcon(getClass().getResource("/resources/asdw.jpg"))));
		jFrame.setLayout(null);
		jFrame.setSize(600, 300);

		ImageIcon configIcon = new ImageIcon(getClass().getResource("/resources/configicon.png"));

		jButtonLoadConfig.setBounds(10, 20, 40, 40);
		jButtonLoadConfig.setIcon(configIcon);
		jButtonLoadConfig.setFocusable(false);
		jButtonLoadConfig.setToolTipText("Load Configurations");

		String dates[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };

		final JComboBox jComboBoxDate = new JComboBox(dates);
		jComboBoxDate.setBounds(100, 30, 70, 20);
		

		String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", };
		final JComboBox jComboBoxMonth = new JComboBox(months);
		jComboBoxMonth.setBounds(180, 30, 100, 20);

		String year[] = { "2015", "2016", "2017", "2018", "2019" };
		final JComboBox jComboBoxYear = new JComboBox(year);
		jComboBoxYear.setBounds(290, 30, 100, 20);

		JLabel jLabelStartTime = new JLabel("Start Time");
		jLabelStartTime.setBounds(100, 120, 100, 20);

		final JTextField jTextFieldStartTime = new JTextField("9:40");
		jTextFieldStartTime.setBounds(100, 150, 100, 20);

		JLabel jLabelEndTime = new JLabel("End Time");
		jLabelEndTime.setBounds(230, 120, 100, 20);

		final JTextField jTextFieldEndTime = new JTextField("10:40");
		jTextFieldEndTime.setBounds(230, 150, 100, 20);

		final JComboBox<String> jComboBoxEnv = new JComboBox<String>();
		jComboBoxEnv.setBounds(100, 80, 150, 20);
		jComboBoxEnv.setFont(new Font("Dialog", Font.BOLD, 13));

		final JLabel jLabelResult = new JLabel("");
		jLabelResult.setBounds(270, 75, 1000, 30);
		jLabelResult.setFont(new Font("Times New Roman", Font.BOLD, 15));

		ImageIcon exportIcon = new ImageIcon(getClass().getResource("/resources/exportb.png"));

		jButtonExportLogs.setBounds(400, 100, 130, 130);
		jButtonExportLogs.setIcon(exportIcon);
		jButtonExportLogs.setFocusable(false);
		jButtonExportLogs.setOpaque(false);
		jButtonExportLogs.setContentAreaFilled(false);
		jButtonExportLogs.setFocusable(false);
		jButtonExportLogs.setBorderPainted(false);
		
		jButtonExportLogs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ConnectionDTO connectionDTO = connect(allEnvdetails, jComboBoxEnv);

				
				Random rand = new Random();
				int tmpFileName = rand.nextInt(5000) + 1;
				String tmpFileNameStr = Integer.toString(tmpFileName);
				tmpFileNameStr = tmpFileNameStr + "_log" + ".txt";
				
				String month = jComboBoxMonth.getSelectedItem().toString();
				String date = jComboBoxDate.getSelectedItem().toString();
				String year = jComboBoxYear.getSelectedItem().toString();

				String queryDate = month + " " + date + "," + " " + year;

			
				String startTime = jTextFieldStartTime.getText();
				String stopTime = jTextFieldEndTime.getText();

				String queryStartDate = queryDate + ", " + startTime;
				String queryStopDate = queryDate + ", " + stopTime;
				
				String logPath = allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString()).getEnvLogPath(); //| awk '$6 == \""+month+"\" && $7 >= 1 && $7 <= 31 {print $9}';
				
				String awkCommand = "cd " + logPath + "; awk '/" + queryStartDate + "/{flag=1;next}/"+ queryStopDate + "/{flag=0}flag' " +"*.*"+  " >> /tmp/"+ tmpFileNameStr + ";";
				new LMSApplicationService().performOperation(connectionDTO, awkCommand);
				
			
				System.out.println(awkCommand);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}	
				String commandRead = "cd /tmp/; cat " + tmpFileNameStr + ";";
				BufferedReader bufferedReaderLogData = new LMSApplicationService().readBufferedUnixFile(connectionDTO, commandRead);
				FileWriter fileWriter = null;
				try {
					if(bufferedReaderLogData.readLine() != null){
						Random random = new Random();
						int randint = random.nextInt(50000) + 1;
						String randStr = Integer.toString(randint);
						try {
							File directory = new File("D:\\LogXtractor\\");
							if(!directory.exists()){
								directory.mkdir();
							}
							fileWriter = new FileWriter("D:\\LogXtractor\\"+randStr+"_log.txt");
							while(bufferedReaderLogData.readLine() != null){
								
								fileWriter.write(bufferedReaderLogData.readLine()+"\n");
								
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						

						JOptionPane.showMessageDialog(null, "File Saved To: D:\\LogXtractor\\" + randStr + ".txt", "DONE",
								JOptionPane.INFORMATION_MESSAGE);
						String removeLogFileCommand = "cd /tmp/;rm -r "+tmpFileNameStr+" "+tmpFileNameStr1+";";
						//new LMSApplicationService().performOperation(connectionDTO, removeLogFileCommand);
						//System.out.println("Removed: "+tmpFileNameStr+" "+tmpFileNameStr1);
						
					}else{
						JOptionPane.showMessageDialog(null, "Please Check Your Date, Start or Stop Time", "No Record Found",
								JOptionPane.INFORMATION_MESSAGE);
						String removeLogFileCommand = "cd /tmp/;rm -r "+tmpFileNameStr+" "+tmpFileNameStr1+";";
						new LMSApplicationService().performOperation(connectionDTO, removeLogFileCommand);
						System.out.println("Removed: "+tmpFileNameStr+" "+tmpFileNameStr1);
					}
				} catch (HeadlessException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		
				
			}
			
		});

		jComboBoxEnv.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub

				ConnectionDTO connectionDTO = connect(allEnvdetails, jComboBoxEnv);
				Session session = new LMSApplicationService().connectServer(connectionDTO, "");
				if (session.isConnected()) {

					ImageIcon successIcon = new ImageIcon(getClass().getResource("/resources/success.png"));
					jLabelSuccess.setBounds(280, 65, 50, 50);
					jLabelSuccess.setIcon(successIcon);
					jLabelSuccess.setFocusable(false);
					jLabelSuccess.setOpaque(false);
					jLabelSuccess.setVisible(true);
					jLabelError.setVisible(false);
				} else {
					jLabelSuccess.setVisible(false);
					jLabelError.setVisible(true);
					ImageIcon errorIcon = new ImageIcon(getClass().getResource("/resources/error.png"));
					jLabelError.setBounds(280, 65, 50, 50);
					jLabelError.setIcon(errorIcon);
					jLabelError.setFocusable(false);
					jLabelError.setOpaque(false);

					// jLabelResult.setText("Auth_Error!");
				}

			}
		});

		jButtonLoadConfig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				int flag = fileChooser.showOpenDialog(jFrame);
				if (flag == JFileChooser.FILES_ONLY) {

					File xmlFile = fileChooser.getSelectedFile();
					String configFilePath = xmlFile.getAbsolutePath();
					System.out.println(configFilePath);
					Document document = loadXMLDocument(xmlFile);
					System.out.println("Root element :" + document.getDocumentElement().getNodeName());
					List<Element> elementsList = getElement(document);
					// System.out.println(elementsList.get(1).getAttribute("name"));
					String[] envNames = new String[elementsList.size()];
					Iterator<Element> iterator = elementsList.iterator();
					DefaultComboBoxModel<String> model = null;
					for (int i = 0; i < elementsList.size(); i++) {

						Element element = iterator.next();
						envNames[i] = element.getAttribute("name");
						// System.out.println(element.getElementsByTagName("url").item(0).getTextContent());

						model = new DefaultComboBoxModel<>(envNames);

					}

					jComboBoxEnv.setModel(model);

					allEnvdetails = getAllEnvDetails(document);
					System.out.println(allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString()).getEnvURL());

					/*
					 * ConnectionDTO connectionDTO = new ConnectionDTO();
					 * connectionDTO.setHost(allEnvdetails.get(jComboBoxEnv.
					 * getSelectedItem().toString()).getEnvURL());
					 * connectionDTO.setPort(Integer.parseInt(allEnvdetails.get(
					 * jComboBoxEnv.getSelectedItem().toString()).getEnvPORT()))
					 * ;
					 * connectionDTO.setUsername(allEnvdetails.get(jComboBoxEnv.
					 * getSelectedItem().toString()).getEnvUSERNAME());
					 * connectionDTO.setPassword(allEnvdetails.get(jComboBoxEnv.
					 * getSelectedItem().toString()).getEnvPASSWORD());
					 * 
					 * URL =
					 * allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString
					 * ()).getEnvURL(); PORT =
					 * allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString
					 * ()).getEnvPORT(); USERNAME =
					 * allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString
					 * ()).getEnvUSERNAME(); PASSWORD =
					 * allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString
					 * ()).getEnvPASSWORD();
					 */
					ConnectionDTO connectionDTO = connect(allEnvdetails, jComboBoxEnv);
					Session session = new LMSApplicationService().connectServer(connectionDTO, "");
					if (session.isConnected()) {

						ImageIcon successIcon = new ImageIcon(getClass().getResource("/resources/success.png"));
						jLabelSuccess.setBounds(280, 65, 50, 50);
						jLabelSuccess.setIcon(successIcon);
						jLabelSuccess.setFocusable(false);
						jLabelSuccess.setOpaque(false);

					} else {

						ImageIcon errorIcon = new ImageIcon(getClass().getResource("/resources/error.png"));
						jLabelError.setBounds(280, 65, 50, 50);
						jLabelError.setIcon(errorIcon);
						jLabelError.setFocusable(false);
						jLabelError.setOpaque(false);

						// jLabelResult.setText("Auth_Error!");
					}

				} else if (flag == JFileChooser.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(null, "Configuration not Loaded", "No File is Selected",
							JOptionPane.INFORMATION_MESSAGE);

				}

			}
		});

		jFrame.add(jButtonExportLogs);
		jFrame.add(jLabelError);
		jFrame.add(jLabelResult);
		jFrame.add(jLabelSuccess);
		jFrame.add(jLabelStartTime);
		jFrame.add(jLabelEndTime);
		jFrame.add(jTextFieldStartTime);
		jFrame.add(jTextFieldEndTime);
		jFrame.add(jComboBoxDate);
		jFrame.add(jComboBoxMonth);
		jFrame.add(jComboBoxYear);
		jFrame.add(jButtonLoadConfig);
		jFrame.add(jComboBoxEnv);
		jFrame.setVisible(true);

	}

	public ConnectionDTO connect(HashMap<String, Environment> allEnvdetails, JComboBox<String> jComboBoxEnv) {

		ConnectionDTO connectionDTO = new ConnectionDTO();
		connectionDTO.setHost(allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString()).getEnvURL());
		connectionDTO
				.setPort(Integer.parseInt(allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString()).getEnvPORT()));
		connectionDTO.setUsername(allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString()).getEnvUSERNAME());
		connectionDTO.setPassword(allEnvdetails.get(jComboBoxEnv.getSelectedItem().toString()).getEnvPASSWORD());

		return connectionDTO;

	}

	public HashMap<String, Environment> getAllEnvDetails(Document document) {

		NodeList nList = document.getElementsByTagName("environment");
		HashMap<String, Environment> envList = new HashMap<String, Environment>();
		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				Environment environment = new Environment();
				environment.setEnvName(eElement.getAttribute("name"));
				environment.setEnvURL(eElement.getElementsByTagName("url").item(0).getTextContent());
				environment.setEnvPORT(eElement.getElementsByTagName("port").item(0).getTextContent());
				environment.setEnvUSERNAME(eElement.getElementsByTagName("username").item(0).getTextContent());
				environment.setEnvPASSWORD(eElement.getElementsByTagName("password").item(0).getTextContent());
				environment.setEnvLogPath(eElement.getElementsByTagName("logpath").item(0).getTextContent());

				envList.put(eElement.getAttribute("name"), environment);

			}

		}

		return envList;
	}

	public List<Element> getElement(Document document) {

		List<Element> listElement = new ArrayList<Element>();
		NodeList nList = document.getElementsByTagName("environment");

		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				listElement.add(eElement);

			}

		}

		return listElement;

	}

	public Document loadXMLDocument(File XMLFile) {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = null;
		Document document = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(XMLFile);
			document.getDocumentElement().normalize();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Invalid XML File Error", "Critical Error", JOptionPane.ERROR_MESSAGE);
		}

		return document;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		XtractorUI xtractorUI = new XtractorUI();

	}

}
