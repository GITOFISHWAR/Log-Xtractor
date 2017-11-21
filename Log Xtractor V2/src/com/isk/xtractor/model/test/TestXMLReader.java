package com.isk.xtractor.model.test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestXMLReader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			File fXmlFile = new File(".\\src\\resources\\configuration.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("environment");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Name : " + eElement.getAttribute("name"));
					System.out.println(
							"URL : " + eElement.getElementsByTagName("url").item(0).getTextContent());
					System.out.println(
							"PORT : " + eElement.getElementsByTagName("port").item(0).getTextContent());
					System.out.println(
							"USER NAME : " + eElement.getElementsByTagName("username").item(0).getTextContent());
					System.out.println("PASSWORD : " + eElement.getElementsByTagName("password").item(0).getTextContent());
					System.out.println("LOGPATH : " + eElement.getElementsByTagName("logpath").item(0).getTextContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
