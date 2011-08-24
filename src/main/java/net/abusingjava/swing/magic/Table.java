package net.abusingjava.swing.magic;

import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingx.JXTable;

import net.abusingjava.swing.MagicPanel;
import net.abusingjava.swing.types.Color;
import net.abusingjava.swing.types.JavaType;
import net.abusingjava.swing.types.Unit;
import net.abusingjava.swing.types.Value;
import net.abusingjava.xml.XmlAttribute;
import net.abusingjava.xml.XmlChildElements;
import net.abusingjava.xml.XmlElement;
import net.abusingjava.xml.XmlTextContent;

@XmlElement("table")
public class Table extends Component {
	
	@XmlAttribute
	boolean $sortable = true;
	
	@XmlAttribute("grid-color")
	Color $gridColor;

	@XmlAttribute
	boolean $editable = false;
		
	@XmlChildElements
	Column[] $columns;
	
	@XmlAttribute("column-control-visible")
	boolean $columnControlVisible = true;

	@SuppressWarnings("rawtypes")
	JTableBinding $binding = null;
	
	@SuppressWarnings({"rawtypes"})
	public void setBinding(final JTableBinding $binding) {
		this.$binding = $binding;
	}
	
	public void clearBinding() {
		if ($binding != null) {
			$binding.unbind();
			$binding = null;
		}
	}
	
	@XmlElement("col")
	public static class Column {
		@XmlAttribute
		JavaType $type = new JavaType(java.lang.Object.class);
		
		@XmlAttribute("min-width")
		Value $minWidth;
		
		@XmlAttribute("max-width")
		Value $maxWidth;
		
		@XmlAttribute("width")
		Value $width;
		
		@XmlTextContent
		String $text = "";
		
		public Class<?> getJavaType() {
			return $type.getJavaType();
		}
	}
	
	public Column getColumn(final String $label) {
		for (Column $c : $columns) {
			if ($c.$text.equals($label)) {
				return $c;
			}
		}
		return null;
	}
	
	@Override
	public void create(final MagicPanel $main, final MagicPanel $parent) {
		
		String[] $columnHeaders = new String[$columns.length];
		for (int $i = 0; $i < $columns.length; $i++) {
			$columnHeaders[$i] = $columns[$i].$text;
		}
		
		TableModel $model = new DefaultTableModel($columnHeaders, 0) {
			private static final long serialVersionUID = -135732270243460558L;

			@Override
			public Class<?> getColumnClass(final int $col) {
				try {
					return $columns[$col].$type.getJavaType();
				} catch (Exception $exc) {
					return Object.class;
				}
			}
		};
		
		JXTable $c = new JXTable($model);
		$c.setEditable($editable);
		$c.setSortable($sortable);
		$c.setColumnControlVisible($columnControlVisible);
		if ($gridColor != null) {
			$c.setGridColor($gridColor.getColor());
		}
		
		for (int $i = 0; $i < $columns.length; $i++) {
			if (($columns[$i].$maxWidth != null) && ($columns[$i].$maxWidth.getUnit() == Unit.PIXEL)) {
				$c.getColumn($columnHeaders[$i]).setMaxWidth($columns[$i].$maxWidth.getValue());
			}
			if (($columns[$i].$minWidth != null)  && ($columns[$i].$maxWidth.getUnit() == Unit.PIXEL)) {
				$c.getColumn($columnHeaders[$i]).setMinWidth($columns[$i].$minWidth.getValue());
			}
			if (($columns[$i].$width != null)  && ($columns[$i].$width.getUnit() == Unit.PIXEL)) {
				$c.getColumn($columnHeaders[$i]).setPreferredWidth($columns[$i].$width.getValue());
			}
		}
		
		
		$realComponent = $c;
		$component = new JScrollPane($c);
		$c.setFillsViewportHeight(true);
		
		super.create($main, $parent);
	}
	
}