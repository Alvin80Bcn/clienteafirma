package es.gob.afirma.signers.pades;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIFactory;

final class PdfUtil {

	private static final Logger LOGGER = Logger.getLogger("es.gob.afirma"); //$NON-NLS-1$

	private static final Set<String> SUPPORTED_SUBFILTERS;
	static {
		SUPPORTED_SUBFILTERS = new HashSet<String>();
		SUPPORTED_SUBFILTERS.add("/ETSI.RFC3161".toLowerCase(Locale.US)); //$NON-NLS-1$
		SUPPORTED_SUBFILTERS.add("/adbe.pkcs7.detached".toLowerCase(Locale.US)); //$NON-NLS-1$
		SUPPORTED_SUBFILTERS.add("/ETSI.CAdES.detached".toLowerCase(Locale.US)); //$NON-NLS-1$
		SUPPORTED_SUBFILTERS.add("/adbe.pkcs7.sha1".toLowerCase(Locale.US)); //$NON-NLS-1$
	}

	private PdfUtil() {
		// No instanciable
	}

	static PdfReader getPdfReader(final byte[] inPDF,
			                      final Properties extraParams,
			                      final boolean headLess) throws BadPdfPasswordException,
			                                                     InvalidPdfException,
			                                                     IOException {
		// Contrasena del propietario del PDF
		final String ownerPassword = extraParams.getProperty("ownerPassword"); //$NON-NLS-1$

		// Contrasena del usuario del PDF
		final String userPassword =  extraParams.getProperty("userPassword"); //$NON-NLS-1$

		PdfReader pdfReader;
		try {
			if (ownerPassword != null) {
				pdfReader = new PdfReader(inPDF, ownerPassword.getBytes());
			}
			else if (userPassword != null) {
				pdfReader = new PdfReader(inPDF, userPassword.getBytes());
			}
			else {
				pdfReader = new PdfReader(inPDF);
			}
		}
		catch (final BadPasswordException e) {
			// Comprobamos que el signer esta en modo interactivo, y si no lo
			// esta no pedimos contrasena por dialogo, principalmente para no interrumpir un firmado por lotes
			// desatendido
			if (headLess) {
				throw new BadPdfPasswordException(e);
			}
			// La contrasena que nos han proporcionada no es buena o no nos
			// proporcionaron ninguna
			final String ownerPwd = new String(
				AOUIFactory.getPassword(
					ownerPassword == null ? CommonPdfMessages.getString("AOPDFSigner.0") : CommonPdfMessages.getString("AOPDFSigner.1"), //$NON-NLS-1$ //$NON-NLS-2$
					null
				)
			);
			if ("".equals(ownerPwd)) { //$NON-NLS-1$
                throw new AOCancelledOperationException(
                    "Entrada de contrasena de PDF cancelada por el usuario", e //$NON-NLS-1$
                );
			}
			try {
				pdfReader = new PdfReader(inPDF, ownerPwd.getBytes());
			}
			catch (final BadPasswordException e2) {
				throw new BadPdfPasswordException(e2);
			}
			extraParams.put("ownerPassword", ownerPwd); //$NON-NLS-1$
		}
		catch (final IOException e) {
			throw new InvalidPdfException(e);
		}
		return pdfReader;

	}

	static void checkPdfCertification(final int pdfCertificationLevel, final Properties extraParams) throws PdfIsCertifiedException {
		if (pdfCertificationLevel != PdfSignatureAppearance.NOT_CERTIFIED &&
				!Boolean.parseBoolean(extraParams.getProperty("allowSigningCertifiedPdfs"))) { //$NON-NLS-1$
			// Si no permitimos dialogos graficos o directamente hemos indicado que no permitimos firmar PDF certificados lanzamos
			// una excepcion
			if (Boolean.parseBoolean(extraParams.getProperty("headLess")) || "false".equalsIgnoreCase(extraParams.getProperty("allowSigningCertifiedPdfs"))) {  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				throw new PdfIsCertifiedException();
			}
			// En otro caso, perguntamos al usuario
			if (AOUIFactory.NO_OPTION == AOUIFactory.showConfirmDialog(
				null,
				CommonPdfMessages.getString("AOPDFSigner.8"), //$NON-NLS-1$
				CommonPdfMessages.getString("AOPDFSigner.9"), //$NON-NLS-1$
				AOUIFactory.YES_NO_OPTION,
				AOUIFactory.WARNING_MESSAGE)
			) {
				throw new AOCancelledOperationException("El usuario no ha permitido la firma de un PDF certificado"); //$NON-NLS-1$
			}
		}
	}

	static void enableLtv(final PdfStamper stp) {
		// PAdES parte 3 seccion 4.7 - Habilitacion para LTV
		stp.getWriter().addDeveloperExtension(
			new PdfDeveloperExtension(
				new PdfName("ESIC"), //$NON-NLS-1$
				PdfWriter.PDF_VERSION_1_7,
				1
			)
		);
	}

	static boolean getAppendMode(final Properties extraParams, final PdfReader pdfReader) {
		if (extraParams.getProperty("ownerPassword") != null || extraParams.getProperty("userPassword") != null) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		return Boolean.parseBoolean(extraParams.getProperty("alwaysCreateRevision")) || pdfReader.getAcroFields().getSignatureNames().size() > 0; //$NON-NLS-1$
	}

	static boolean pdfHasUnregisteredSignatures(final byte[] pdf, final Properties xParams) throws InvalidPdfException, BadPdfPasswordException, IOException {
		final Properties extraParams = xParams != null ? xParams : new Properties();
		final PdfReader pdfReader = PdfUtil.getPdfReader(
			pdf,
			extraParams,
			Boolean.getBoolean(extraParams.getProperty("headLess")) //$NON-NLS-1$
		);
		return pdfHasUnregisteredSignatures(pdfReader);
	}

	static boolean pdfHasUnregisteredSignatures(final PdfReader pdfReader) {

		boolean ret = false;
    	for (int i = 0; i < pdfReader.getXrefSize(); i++) {
    		final PdfObject pdfobj = pdfReader.getPdfObject(i);
    		if (pdfobj != null && pdfobj.isDictionary()) {
    			final PdfDictionary d = (PdfDictionary) pdfobj;
    			if (PdfName.SIG.equals(d.get(PdfName.TYPE))) {

    				final String subFilter = d.get(PdfName.SUBFILTER) != null ?
    						d.get(PdfName.SUBFILTER).toString().toLowerCase(Locale.US) : null;

    				if (subFilter == null || !SUPPORTED_SUBFILTERS.contains(subFilter)) {
    					ret = true;
	    				try {
	    					final PdfObject o = d.get(PdfName.CERT);
	    					final byte[] data;
	    					if (o instanceof PdfString) {
	    						data = ((PdfString)o).getOriginalBytes();
	    					}
	    					else {
	    						data = ((PdfString) ((PdfArray) d.get(PdfName.CERT)).getArrayList().get(0)).getOriginalBytes();
	    					}

							final X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate( //$NON-NLS-1$
								new ByteArrayInputStream(
									data
								)
							);
							LOGGER.info(
								"Encontrada firma no registrada, hecha con certificado emitido por: " + cert.getIssuerX500Principal().toString() //$NON-NLS-1$
							);
						}
	    				catch (final Exception e) {
							LOGGER.warning("No se ha podido comprobar la identidad de una firma no registrada con el subfiltro: " + subFilter + ": " + e); //$NON-NLS-1$ //$NON-NLS-2$
						}
    				}
    			}
    		}
    	}
    	return ret;
	}
}
