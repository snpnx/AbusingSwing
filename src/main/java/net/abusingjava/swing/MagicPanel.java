package net.abusingjava.swing;

import java.io.InputStream;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;

import net.abusingjava.swing.magic.*;
import net.abusingjava.swing.magic.Binding.Property;
import net.abusingjava.xml.AbusingXML;


public class MagicPanel extends JPanel {
	
	public static enum Orientation {
		VERTICAL, HORIZONTAL, FIXED
	}
	
	private static final long serialVersionUID = -1112524113641835259L;
	
	private final Orientation $orientation;
	private final MagicPanel $main;
	private final Container $definition;
	private final Panel $panel;
	
	Map<String,Component> $componentsByName = new HashMap<String,Component>();
	ArrayList<Component> $myComponents = new ArrayList<Component>();

	private Object $invocationHandler = null;
	
	public MagicPanel(final InputStream $stream) {
		if ($stream == null) {
			throw new IllegalArgumentException("$stream may not be null.");
		}
		$panel = AbusingXML.loadXML($stream, Panel.class);

		$main = this;
		this.$orientation = determineOrientation($panel.getContainer());
		this.$definition = $panel.getContainer();

		this.$definition.create(this, null);

		setLayout(new MagicLayoutManager(this, this, $panel.getContainer()));
		
		buildPanel();
	}
	
	public MagicPanel(final Panel $panel) {
		if ($panel == null) {
			throw new IllegalArgumentException("$panel must not be null.");
		}
		this.$panel = $panel;
		
		$main = this;
		this.$orientation = determineOrientation($panel.getContainer());
		this.$definition = $panel.getContainer();

		this.$definition.create(this, null);
		
		setLayout(new MagicLayoutManager(this, this, $panel.getContainer()));
		
		buildPanel();
	}
	
	public MagicPanel(final MagicPanel $main, final Container $container) {
		if ($main == null) {
			throw new IllegalArgumentException("Won’t construct a panel which is not the root panel without a parent ($main may not be null).");
		}
		this.$panel = $main.getDefinitionPanel();
		
		this.$main = $main;
		this.$orientation = determineOrientation($container);
		this.$definition = $container;
		
		setLayout(new MagicLayoutManager($main, this, $container));
		
		buildPanel();
	}
	
	
	private static Orientation determineOrientation(final Container $container) {
		if ($container instanceof HBox) {
			return Orientation.HORIZONTAL;
		} else if ($container instanceof VBox) {
			return Orientation.VERTICAL;
		}
		return Orientation.FIXED;
	}
	
	public Container getDefinition() {
		return $definition;
	}
	
	@Override
	public boolean isOptimizedDrawingEnabled() {
		return $orientation != Orientation.FIXED;
	}

	private void buildPanel() {
		for (Component $c : $definition) {
			$c.create($main, this);
			add($c.getJComponent());
		}
	}
	int $noName = 0;
	
	public void registerComponent(String $name, final Component $component) {
		if ($name == null) {
			$name = "#" + $noName++;
		}
		$componentsByName.put($name, $component);
	}
	
	public Components $(final String $selector) {
		if ($selector.equals("*")) {
			return new Components($componentsByName.values());
		} else if ($selector.startsWith("#")) {
			Component $c = $componentsByName.get($selector.substring(1));
			if ($c != null) {
				return new Components($c);
			}
			return new Components();
		} else if ($selector.startsWith(".")) {
			
		}
		return null;
	}
	
	public Components $(final Class<?> $componentClass) {
		List<Component> $components = new LinkedList<Component>();
		for (Component $c : $componentsByName.values()) {
			if (($componentClass.isAssignableFrom($c.getJComponent().getClass()))
					|| ($componentClass.isAssignableFrom($c.getRealComponent().getClass()))
					|| ($componentClass.isAssignableFrom($c.getClass()))) {
				$components.add($c);
			}
		}
		return new Components($components);
	}
	
	public Orientation getOrientation() {
		return $orientation;
	}
	
	public MagicPanel hideAll() {
		$("*").hide();
		return this;
	}
	
	public MagicPanel showAll() {
		$("*").show();
		return this;
	}
	
	public MagicPanel enableAll() {
		$("*").enable();
		return this;
	}
	
	public MagicPanel disableAll() {
		$("*").disable();
		return this;
	}

	public MagicPanel setInvocationHandler(final Object $object) {
		$invocationHandler = $object;
		return this;
	}
	
	public Object getInvocationHandler() {
		return $invocationHandler;
	}
	
	public Panel getDefinitionPanel() {
		return $panel;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public MagicPanel bind(final String $bindingName, final Object $object) {
		Binding $b = $panel.getBinding($bindingName);
		if ($b == null) {
			System.err.println("Binding: no such binding found");
			return this;
		}
		
		if ($b.isTableBinding() && ($object instanceof List)) {
			Table $tableDefinition = (Table) $main.$componentsByName.get($b.getTableName());
			JTable $table = $main.$("#" + $b.getTableName()).as(JTable.class);
			
			$tableDefinition.clearBinding();

			JTableBinding $tableBinding = SwingBindings.createJTableBinding(
					AutoBinding.UpdateStrategy.READ_WRITE, (List<?>) $object, $table);
			
			for (Property $p : $b) {
				ColumnBinding $columnBinding = $tableBinding.addColumnBinding(
						BeanProperty.create($p.getName()));
				$columnBinding.setColumnName($p.getTarget());
				$columnBinding.setColumnClass($tableDefinition.getColumn($p.getTarget()).getJavaType());
			}
			
			$tableDefinition.setBinding($tableBinding);
			$tableBinding.bind();
		}
		
		return this;
	}
	
	public static void main(final String... $args) {
		AbusingSwing.setNimbusLookAndFeel();
		AbusingSwing.showWindow("MagicPanel.xml");
		/*
		JFrame $f = new JFrame();
		$f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MagicPanel $m = MagicFactory.makePanel(MagicPanel.class.getResourceAsStream("MagicPanel.xml"));
		$f.setContentPane($m);
		
		$f.setMinimumSize(new Dimension(300, 200));
		$f.setSize(600, 400);
		$f.setVisible(true);
		*/
	}

}
