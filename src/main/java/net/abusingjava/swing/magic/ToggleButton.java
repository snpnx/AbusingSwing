package net.abusingjava.swing.magic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import net.abusingjava.swing.MagicPanel;
import net.abusingjava.swing.types.Value;
import net.abusingjava.xml.XmlAttribute;
import net.abusingjava.xml.XmlElement;

@XmlElement("togglebutton")
public class ToggleButton extends TextComponent {

	@XmlAttribute
	Boolean $selected;
	
	@XmlAttribute
	String $filters;
	
	@Override
	public void create(final MagicPanel $main, final MagicPanel $parent) {
		if ($height == null) {
			$height = new Value("27px");
		}
		if ($selected == null) {
			$selected = false;
		}
		
		final JToggleButton $c = new JToggleButton($text, $selected);
		
		if ($filters != null) {
			final String $filter = $filters.charAt(0) == '#' ? $filters.substring(1) : $filters;
			$c.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent $ev) {
					$main.$("#" + $filter).showSelectedOnly($c.isSelected());
				}
			});
		}
		
		$component = $c;
				
		super.create($main, $parent);
	}
	
}