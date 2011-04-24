/*
 * Este fichero forma parte del Cliente @firma. 
 * El Cliente @firma es un applet de libre distribuci�n cuyo c�digo fuente puede ser consultado
 * y descargado desde www.ctt.map.es.
 * Copyright 2009,2010 Ministerio de la Presidencia, Gobierno de Espa�a (opcional: correo de contacto)
 * Este fichero se distribuye bajo las licencias EUPL versi�n 1.1  y GPL versi�n 3  seg�n las
 * condiciones que figuran en el fichero 'licence' que se acompa�a.  Si se   distribuyera este 
 * fichero individualmente, deben incluirse aqu� las condiciones expresadas all�.
 */

package es.gob.afirma.actions;

import java.security.PrivilegedAction;

/**
 * Clase privilegiada b&aacute;sica. Implementa las funciones necesarias para conocer
 * si la operaci&oacute;n produjo alg&uacute;n error durante su ejecuci&oacute;n. 
 */
public abstract class BasicPrivilegedAction<T, U> implements PrivilegedAction<T> {

	/** Resultado de la operaci&oacute;n. */
	private U result = null;
	
	/** Mensaje de error almacenado. */
	private String errorMsg = null;
	
	/** Excepci&oacute;n que produjo el problema. */
	private Exception exception = null;
	
	/**
	 * Establece el resultado de la operaci&oacute;n.
	 * @param result Resultado de la operaci&oacute;n.
	 */
	protected void setResult(U result) {
		this.result = result;
	}
	
	/**
	 * Recupera el resultado de la operaci&oacute;n. Si se produjo alg&uacute;n error o si
	 * la operaci&oacute;n no produjo ning&uacute;n error, devuelve {@code null}.
	 * @return Resultado de la operaci&oacute;n.
	 */
	public U getResult() {
		return result;
	}
	
	/** Indica si ocurri&oacute; alg&uacute;n error. */
	public boolean isError() {
		return errorMsg != null;
	}
	
	/**
	 * Establece el mensaje del error ocurrido. Si el mensaje es {@code null} se entender&aacute;a que
	 * no ocurri&oacute; ning&uacute;n error.
	 * @param errorMsg Mensaje de error.
	 */
	protected void setError(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/**
	 * Establece el mensaje del error ocurrido y la excepci&oacute;n que lo caus&oacute;. Si el
	 * mensaje es {@code null} se entender&aacute;a que no ocurri&oacute; ning&uacute;n error.
	 * @param errorMsg Mensaje de error.
	 * @param e Excepci&oacute;n que caus&oacute; el error.
	 */
	protected void setError(String errorMsg, Exception e) {
		this.errorMsg = errorMsg;
		this.exception = e;		
	}
	
	/**
	 * Recupera el mensaje del error ocurrido.
	 * @return Mensaje de error.
	 */
	public String getErrorMessage() {
		return errorMsg;
	}
	
	/**
	 * Recupera la excepci&oacute;n que produjo el error. Si no se estableci&oacute; se
	 * devolver&aacute; {@code null}
	 * @return Excepci&oacute;n que produjo el error.
	 */
	public Exception getException() {
		return exception;
	}
}
