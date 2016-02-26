package jMESYS.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class jMESYSPokeDialog extends JDialog implements ActionListener {
	
	private JDialog frame;
	
	private int option=0;
	private JTextField AddressBox;
	private JTextField ValueBox;
	
	public String getAddress() {
		return AddressBox.getText();
	}

	public void setAddress(String address) {
		AddressBox.setText(address);
	}

	public String getValue() {
		return ValueBox.getText();
	}

	public void setValue(String value) {
		ValueBox.setText(value);
	}

	public int getOption() {
		return option;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public jMESYSPokeDialog(Frame frame2, String title, boolean modal) {
		super(frame2, title, modal);
		
		// address
		JPanel panSup = new JPanel();
		panSup.setLayout(new FlowLayout());
				
		JLabel labelA = new JLabel();
		labelA.setText("Address");		
		AddressBox = new JTextField(5);
		
		// value
		JLabel labelV = new JLabel();
		labelV.setText("Value");		
		ValueBox = new JTextField(3);
		
		panSup.add(labelA);
		panSup.add(AddressBox);
		panSup.add(labelV);
		panSup.add(ValueBox);
		
		// Buttons Panel
		JPanel panButtons = new JPanel();
		JButton butOK = new JButton("OK");
		butOK.addActionListener(this);
		JButton butCancel = new JButton("Cancel");
		butCancel.addActionListener(this);
		panButtons.add(butOK);
		panButtons.add(butCancel);
		
		frame = new JDialog(frame2, title, modal);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panSup,BorderLayout.CENTER);
		frame.getContentPane().add(panButtons,BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent ev) {
		System.out.println(ev.getActionCommand());
		if (ev.getActionCommand().equals("OK")){
			option = 1;
			frame.dispose();
		} else if (ev.getActionCommand().equals("Cancel")){
			option = 0;
			frame.dispose();
		}
		
	}

	public int showOpenDialog() {
		frame.pack();
		//this.setSize(650, 268);
		frame.setVisible(true);
		
		return option;
	}

}
