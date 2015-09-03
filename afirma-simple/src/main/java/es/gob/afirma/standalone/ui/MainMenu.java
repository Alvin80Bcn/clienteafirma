/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * Date: 11/01/11
 * You may contact the copyright holder at: soporte.afirma5@mpt.es
 */

package es.gob.afirma.standalone.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirma;
import es.gob.afirma.standalone.SimpleAfirmaMessages;

/** Barra de men&uacute; para toda la aplicaci&oacute;n.
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s */
public final class MainMenu extends JMenuBar {

    private static final long serialVersionUID = -8361808353554036015L;

    private final JMenu menuArchivo = new JMenu();
    private final JMenuItem firmarMenuItem = new JMenuItem();
    private final JMenuItem abrirMenuItem = new JMenuItem();
    private final JMenuItem ayudaMenuItem = new JMenuItem();

    private final JMenu menuAyuda = new JMenu(SimpleAfirmaMessages.getString("MainMenu.9"));  //$NON-NLS-1$

    private final JFrame parent;
    JFrame getParentComponent() {
    	return this.parent;
    }

    private final SimpleAfirma saf;
    SimpleAfirma getSimpleAfirma() {
    	return this.saf;
    }

    /** Indica si hay alg&uacute; men&uacute; de primer nivel seleccionado.
     * @return <code>true</code> si hay alg&uacute; men&uacute; de primer nivel seleccionado,
     *         <code>false</code> en caso contrario */
    public boolean isAnyMenuSelected() {
    	return this.menuArchivo.isSelected() || this.menuAyuda.isSelected();
    }

    /** Construye la barra de men&uacute; de la aplicaci&oacute;n.
     * En MS-Windows y Linux se crean los siguientes atajos de teclado:
     * <ul>
     *  <li>Alt+A = Menu archivo</li>
     *  <li>
     *   <ul>
     *    <li>Alt+B = Abrir archivo</li>
     *    <li>Alt+I = Firmar archivo</li>
     *    <li>Alt+F4 = Salir del programa</li>
     *   </ul>
     *  </li>
     *  <li>Alt+Y = Menu Ayuda</li>
     *  <li>
     *   <ul>
     *    <li>Alt+U = Ayuda</li>
     *    <li>Alt+R = Acerca de...</li>
     *   </ul>
     *  </li>
     *  <li>Alt+S = Seleccionar fichero</li>
     *  <li>Alt+F = Firmar fichero</li>
     *  <li>Ctrl+A = Seleccionar fichero</li>
     *  <li>Ctrl+F = Firmar fichero</li>
     *  <li>Alt+F4 = Salir del programa</li>
     *  <li>F1 = Ayuda</li>
     *  <li>Ctrl+R = Acerca de...</li>
     * </ul>
     * @param p Componente padre para la modalidad
     * @param s Aplicaci&oacute;n padre, para determinar el n&uacute;mero de
     *        locales e invocar a ciertos comandos de men&uacute; */
    public MainMenu(final JFrame p, final SimpleAfirma s) {
        this.saf = s;
        this.parent = p;
        // Importante: No cargar en un invokeLater, da guerra en Mac OS X
        createUI();
    }

    private void createUI() {

        final boolean isMac = Platform.OS.MACOSX.equals(Platform.getOS());

        this.menuArchivo.setText(SimpleAfirmaMessages.getString("MainMenu.0")); //$NON-NLS-1$
        this.menuArchivo.setMnemonic(KeyEvent.VK_A);
        this.menuArchivo.getAccessibleContext().setAccessibleDescription(
    		SimpleAfirmaMessages.getString("MainMenu.1") //$NON-NLS-1$
        );
        this.menuArchivo.setEnabled(true);

        this.abrirMenuItem.setText(SimpleAfirmaMessages.getString("MainMenu.2")); //$NON-NLS-1$
        this.abrirMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.abrirMenuItem.getAccessibleContext().setAccessibleDescription(
    		SimpleAfirmaMessages.getString("MainMenu.3") //$NON-NLS-1$
		);
        this.abrirMenuItem.addActionListener(
    		new ActionListener() {
	        	/** {@inheritDoc} */
	            @Override
	            public void actionPerformed(final ActionEvent ae) {
	            	final File fileToLoad;
	            	try {
	            		fileToLoad = AOUIFactory.getLoadFiles(
	            			SimpleAfirmaMessages.getString("MainMenu.4"), //$NON-NLS-1$
	            			MainMenu.this.getSimpleAfirma().getCurrentDir() != null ? MainMenu.this.getSimpleAfirma().getCurrentDir().getAbsolutePath() : null,
	            			null,
	            			null,
	            			null,
	            			false,
	            			false,
	            			null,
	            			MainMenu.this
	        			)[0];
	            	}
	            	catch(final AOCancelledOperationException e) {
	            		return;
	            	}
	            	MainMenu.this.getSimpleAfirma().loadFileToSign(fileToLoad);
	            }
	        }
		);
        this.menuArchivo.add(this.abrirMenuItem);

        this.firmarMenuItem.setText(SimpleAfirmaMessages.getString("MainMenu.5")); //$NON-NLS-1$
        this.firmarMenuItem.setAccelerator(
    		KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
		);
        this.firmarMenuItem.getAccessibleContext().setAccessibleDescription(
    		SimpleAfirmaMessages.getString("MainMenu.6") //$NON-NLS-1$
        );
        this.firmarMenuItem.setEnabled(false);
        this.firmarMenuItem.addActionListener(
    		new ActionListener() {
	        	/** {@inheritDoc} */
	            @Override
	            public void actionPerformed(final ActionEvent e) {
	                MainMenu.this.getSimpleAfirma().signLoadedFile();
	            }
	        }
		);
        this.menuArchivo.add(this.firmarMenuItem);

        // En Mac OS X el salir lo gestiona el propio OS
        if (!isMac) {
            this.menuArchivo.addSeparator();
            final JMenuItem salirMenuItem = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.7")); //$NON-NLS-1$
            salirMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
            salirMenuItem.getAccessibleContext().setAccessibleDescription(
        		SimpleAfirmaMessages.getString("MainMenu.8") //$NON-NLS-1$
             );
            salirMenuItem.addActionListener(
        		new ActionListener() {
	            	/** {@inheritDoc} */
	                @Override
	                public void actionPerformed(final ActionEvent ae) {
	                    exitApplication();
	                }
	            }
    		);
            salirMenuItem.setMnemonic(KeyEvent.VK_L);
            this.menuArchivo.add(salirMenuItem);
        }

        this.add(this.menuArchivo);

        if (!isMac) {
            final JMenu optionsMenu = new JMenu(SimpleAfirmaMessages.getString("MainMenu.18")); //$NON-NLS-1$
            optionsMenu.setMnemonic(KeyEvent.VK_O);
            optionsMenu.getAccessibleContext().setAccessibleDescription(
        		SimpleAfirmaMessages.getString("MainMenu.19") //$NON-NLS-1$
            );

            final JMenuItem preferencesMenuItem = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.12")); //$NON-NLS-1$
            preferencesMenuItem.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
    		);
            preferencesMenuItem.setMnemonic(KeyEvent.VK_P);
            preferencesMenuItem.getAccessibleContext().setAccessibleDescription(
        		SimpleAfirmaMessages.getString("MainMenu.16") //$NON-NLS-1$
    		);
            preferencesMenuItem.addActionListener(
        		new ActionListener() {
	            	/** {@inheritDoc} */
					@Override
					public void actionPerformed(final ActionEvent ae) {
					    showPreferences();
					}
				}
    		);
            optionsMenu.add(preferencesMenuItem);

            this.add(optionsMenu);
        }
        // En Mac OS X el menu es "Preferencias" dentro de la opcion principal
        else {
            Application.getApplication().setPreferencesHandler(
        		new PreferencesHandler() {
	            	/** {@inheritDoc} */
	                @Override
	                public void handlePreferences(final PreferencesEvent pe) {
	                    showPreferences();
	                }
	            }
    		);
        }

        // Separador para que la ayuda quede a la derecha, se ignora en Mac OS X
        this.add(Box.createHorizontalGlue());

        this.menuAyuda.setMnemonic(KeyEvent.VK_Y);
        this.menuAyuda.getAccessibleContext().setAccessibleDescription(
          SimpleAfirmaMessages.getString("MainMenu.10") //$NON-NLS-1$
        );

        this.ayudaMenuItem.setText(SimpleAfirmaMessages.getString("MainMenu.11")); //$NON-NLS-1$
        this.ayudaMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1")); //$NON-NLS-1$
        this.ayudaMenuItem.getAccessibleContext().setAccessibleDescription(
              SimpleAfirmaMessages.getString("MainMenu.13") //$NON-NLS-1$
        );
        this.ayudaMenuItem.addActionListener(
    		new ActionListener() {
	            @Override
	            public void actionPerformed(final ActionEvent e) {
	                SimpleAfirma.showHelp();
	            }
	        }
		);
        this.menuAyuda.add(this.ayudaMenuItem);

        // En Mac OS X el Acerca de lo gestiona el propio OS
        if (!isMac) {
            this.menuAyuda.addSeparator();
            final JMenuItem acercaMenuItem = new JMenuItem(SimpleAfirmaMessages.getString("MainMenu.15")); //$NON-NLS-1$
            acercaMenuItem.getAccessibleContext().setAccessibleDescription(
        		SimpleAfirmaMessages.getString("MainMenu.17") //$NON-NLS-1$
            );
            acercaMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            acercaMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    showAbout(MainMenu.this.getParentComponent() == null ? MainMenu.this : MainMenu.this.getParentComponent());
                }
            });
            acercaMenuItem.setMnemonic(KeyEvent.VK_R);
            this.menuAyuda.add(acercaMenuItem);
            this.add(this.menuAyuda);
        }

        // Los mnemonicos en elementos de menu violan las normas de interfaz de Apple,
        // asi que prescindimos de ellos en Mac OS X
        if (!isMac) {
            this.abrirMenuItem.setMnemonic(KeyEvent.VK_B);
            this.ayudaMenuItem.setMnemonic(KeyEvent.VK_U);
            this.firmarMenuItem.setMnemonic(KeyEvent.VK_F);
        }
        // Acciones especificas de Mac OS X
        else {
            Application.getApplication().setAboutHandler(
        		new AboutHandler() {
	                @Override
	                public void handleAbout(final AboutEvent ae) {
	                    showAbout(MainMenu.this.getParentComponent() == null ? MainMenu.this : MainMenu.this.getParentComponent());
	                }
	            }
    		);
            Application.getApplication().setQuitHandler(
        		new QuitHandler() {
	                @Override
	                public void handleQuitRequestWith(final QuitEvent qe, final QuitResponse qr) {
	                    if (!exitApplication()) {
	                        qr.cancelQuit();
	                    }
	                }
        		}
    		);
        }

    }

    /** Habilita o deshabilita el men&uacute; de operaciones sobre ficheros.
     * @param en <code>true</code> para habilitar las operaciones sobre ficheros, <code>false</code> para deshabilitarlas */
    public void setEnabledOpenCommand(final boolean en) {
        if (this.abrirMenuItem != null) {
            this.abrirMenuItem.setEnabled(en);
        }
    }

    /** Habilita o deshabilita el elemento de men&uacute; de firma de fichero.
     * @param en <code>true</code> para habilitar el elemento de men&uacute; de firma de fichero, <code>false</code> para deshabilitarlo */
    public void setEnabledSignCommand(final boolean en) {
        if (this.firmarMenuItem != null) {
            this.firmarMenuItem.setEnabled(en);
        }
    }

    void showPreferences() {
        final JDialog preferencesDialog = new JDialog(MainMenu.this.getParentComponent(), true);
        preferencesDialog.setTitle(SimpleAfirmaMessages.getString("MainMenu.24")); //$NON-NLS-1$
        preferencesDialog.add(new PreferencesPanel(preferencesDialog));
        preferencesDialog.setSize(800, 610);
        preferencesDialog.setResizable(false);
        preferencesDialog.setLocationRelativeTo(MainMenu.this.getParentComponent());
        preferencesDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        preferencesDialog.setVisible(true);
    }

    /** Muestra en OS X el men&uacute; "Acerca de...".
     * @param parentComponent Componente padre para la modalidad. */
    public static void showAbout(final Component parentComponent) {
        AOUIFactory.showMessageDialog(
    		parentComponent,
			SimpleAfirmaMessages.getString("MainMenu.14", SimpleAfirma.getVersion(), System.getProperty("java.version")), //$NON-NLS-1$ //$NON-NLS-2$,
            SimpleAfirmaMessages.getString("MainMenu.15"), //$NON-NLS-1$
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    boolean exitApplication() {
        return this.saf.askForClosing();
    }

}
