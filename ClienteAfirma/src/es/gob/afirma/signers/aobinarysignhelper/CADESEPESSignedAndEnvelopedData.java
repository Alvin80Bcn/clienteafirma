/*
 * Este fichero forma parte del Cliente @firma. 
 * El Cliente @firma es un applet de libre distribuci�n cuyo c�digo fuente puede ser consultado
 * y descargado desde www.ctt.map.es.
 * Copyright 2009,2010 Ministerio de la Presidencia, Gobierno de Espa�a (opcional: correo de contacto)
 * Este fichero se distribuye bajo las licencias EUPL versi�n 1.1  y GPL versi�n 3  seg�n las
 * condiciones que figuran en el fichero 'licence' que se acompa�a.  Si se   distribuyera este 
 * fichero individualmente, deben incluirse aqu� las condiciones expresadas all�.
 */

package es.gob.afirma.signers.aobinarysignhelper;

import static es.gob.afirma.signers.aobinarysignhelper.SigUtils.getAttributeSet;
import static es.gob.afirma.signers.aobinarysignhelper.SigUtils.makeAlgId;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.ietf.jgss.Oid;

import sun.security.x509.AlgorithmId;
import es.gob.afirma.ciphers.AOCipherConfig;
import es.gob.afirma.exceptions.AOException;
import es.gob.afirma.misc.AOCryptoUtil;


/**
 * Clase que implementa firma digital CADES-EPES SignedAndEnvelopedData. basado
 * en las especificaciones de RFC-5126.
 *
 * La Estructura del mensaje es la siguiente:<br>
 *
 * <pre><code>
 * SignedAndEnvelopedData ::= SEQUENCE {
 *    version Version,
 *    recipientInfos RecipientInfos,
 *    digestAlgorithms DigestAlgorithmIdentifiers,
 *    encryptedContentInfo EncryptedContentInfo,
 *    certificates
 *      [0] IMPLICIT ExtendedCertificatesAndCertificates
 *         OPTIONAL,
 *    crls
 *      [1] IMPLICIT CertificateRevocationLists OPTIONAL,
 *    signerInfos SignerInfos }
 *
 * </code></pre>
 *
 * Todos los datos son iguales que en SignedAndEnvelopedData de Pkcs#7 a excepci&oacute;n
 * de que en  dentro de signerInfo:
 *
 * <pre><code>
 * SignerInfo ::= SEQUENCE {
 *       version CMSVersion,
 *       sid SignerIdentifier,
 *       digestAlgorithm DigestAlgorithmIdentifier,
 *       signedAttrs [0] IMPLICIT SignedAttributes OPTIONAL,
 *       signatureAlgorithm SignatureAlgorithmIdentifier,
 *       signature SignatureValue,
 *       unsignedAttrs [1] IMPLICIT UnsignedAttributes OPTIONAL }
 *</code></pre>
 *
 * En los atributos de la firma (signedAttrs) va la pol&iacute;tica de la firma.
 *
 * id-aa-ets-sigPolicyId OBJECT IDENTIFIER ::= { iso(1)
 *     member-body(2) us(840) rsadsi(113549) pkcs(1) pkcs9(9)
 *     smime(16) id-aa(2) 15 }
 *
 *
 *
 * La implementaci&oacute;n del c&oacute;digo ha seguido los pasos necesarios para crear un
 * mensaje SignedAndEnvelopedData de BouncyCastle: <a href="http://www.bouncycastle.org/">www.bouncycastle.org</a>
 */

public final class CADESEPESSignedAndEnvelopedData  {

	/**
	 * Clave de cifrado. La almacenamos internamente.
	 */
	private SecretKey cipherKey;
	ASN1Set signedAttr2;

	/**
	 * M&eacute;todo que genera la firma de tipo SignedAndEnvelopedData.
	 *
	 * @param parameters Par&aacute;metros necesarios para la generaci&oacute;n de este tipo.
	 * @param config     Configuraci&oacute;n del algoritmo para firmar
	 * @param policy     Pol&iacute;tica del certificado.
	 * @param qualifier		OID de la pol&iacute;tica.
	 * @param signingCertificateV2 <code>true</code> si se desea usar la versi&oacute;n 2 del atributo <i>Signing Certificate</i>
	 *                             <code>false</code> para usar la versi&oacute;n 1
	 * @param certDest   Certificado del destino al cual va dirigido la firma.
	 * @param dataType   Identifica el tipo del contenido a firmar.
	 * @param keyEntry   Entrada a la clave de firma
	 *
	 * @return           Firma de tipo SignedAndEnvelopedData.
	 * @throws java.io.IOException Si ocurre alg&uacute;n problema leyendo o escribiendo los datos
	 * @throws java.security.cert.CertificateEncodingException Si se produce alguna excepci&oacute;n con los certificados de firma.
	 * @throws java.security.NoSuchAlgorithmException Si no se encuentra un algoritmo v&aacute;lido.
	 */
	public byte[] genCADESEPESSignedAndEnvelopedData(P7ContentSignerParameters parameters, AOCipherConfig config, 
			String policy, Oid qualifier, boolean signingCertificateV2,X509Certificate[] certDest, Oid dataType, 
			PrivateKeyEntry keyEntry) throws IOException, CertificateEncodingException, NoSuchAlgorithmException {

		this.cipherKey = Utils.initEnvelopedData(config, certDest);

		// 1. VERSION
		// la version se mete en el constructor del signedAndEnvelopedData y es 1

		// 2. DIGESTALGORITM
		// buscamos que timo de algoritmo es y lo codificamos con su OID

		String signatureAlgorithm = null;
		String digestAlgorithm = null;
		AlgorithmId digestAlgorithmId = null;
		ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
		String keyAlgorithm = null;
		
		try {
			signatureAlgorithm = parameters.getSignatureAlgorithm();
			digestAlgorithm = AOCryptoUtil.getDigestAlgorithmName(signatureAlgorithm);
			digestAlgorithmId = AlgorithmId.get(digestAlgorithm);
			keyAlgorithm = Utils.getKeyAlgorithm(signatureAlgorithm, digestAlgorithmId);
			
			AlgorithmIdentifier digAlgId = makeAlgId(digestAlgorithmId.getOID().toString(), digestAlgorithmId.getEncodedParams());
			digestAlgs.add(digAlgId);
		}
		catch (final Throwable e) {
			throw new IOException("Error de codificacion: " + e);
		}


		// LISTA DE CERTIFICADOS: obtenemos la lista de certificados
		ASN1Set certificates = null;
		X509Certificate[] signerCertificateChain = parameters.getSignerCertificateChain();

		certificates = Utils.fetchCertificatesList(signerCertificateChain);

		// 2.   RECIPIENTINFOS
		Info infos = Utils.initVariables(parameters.getContent(), config, certDest, cipherKey);

		// 4. SIGNERINFO
		// raiz de la secuencia de SignerInfo
		ASN1EncodableVector signerInfos = new ASN1EncodableVector();

		TBSCertificateStructure tbs2 = TBSCertificateStructure.getInstance(ASN1Object.fromByteArray(signerCertificateChain[0].getTBSCertificate()));

		IssuerAndSerialNumber encSid = new IssuerAndSerialNumber(tbs2.getIssuer(), tbs2.getSerialNumber().getValue());

		SignerIdentifier identifier = new SignerIdentifier(encSid);

		//AlgorithmIdentifier
		AlgorithmIdentifier digAlgId = new AlgorithmIdentifier(new DERObjectIdentifier(digestAlgorithmId.getOID().toString()), new DERNull());

		//// ATRIBUTOS
		ASN1EncodableVector contextExpecific = Utils.generateSignerInfo(
    			signerCertificateChain[0],
    			digestAlgorithmId,
    			digestAlgorithm,
    			digAlgId,
    			parameters.getContent(),
    			policy,
    			qualifier,
    			signingCertificateV2,
    			dataType,
    			null
    	);
    	signedAttr2 = getAttributeSet(new AttributeTable(contextExpecific));
    	ASN1Set signedAttr = getAttributeSet(new AttributeTable(contextExpecific));

		//digEncryptionAlgorithm
		AlgorithmId digestAlgorithmIdEnc = AlgorithmId.get(keyAlgorithm);
		AlgorithmIdentifier encAlgId;

		try {
			encAlgId = makeAlgId(digestAlgorithmIdEnc.getOID().toString(), digestAlgorithmIdEnc.getEncodedParams());
		}
		catch (Exception e) {
			throw new IOException("Error de codificacion: " + e);
		}

		ASN1OctetString sign2= null;
		try {
			sign2 = firma(signatureAlgorithm, keyEntry);
		} catch (AOException ex) {
			Logger.getLogger(GenSignedData.class.getName()).log(Level.SEVERE, null, ex);
		}

		signerInfos.add(
				new SignerInfo(
						identifier,
						digAlgId,
						signedAttr,
						encAlgId,
						sign2,
						null //unsignedAttr
				)
		);

		ASN1Set certrevlist = null;

		// construimos el Signed And Enveloped Data y lo devolvemos
		return new ContentInfo(
				PKCSObjectIdentifiers.signedAndEnvelopedData,
				new SignedAndEnvelopedData(
						new DERSet(infos.getRecipientInfos()),
						new DERSet(digestAlgs),
						infos.getEncInfo(),
						certificates,
						certrevlist,
						new DERSet(signerInfos)
				)
		).getDEREncoded();
	}

	/**
	 * Realiza la firma usando los atributos del firmante.
	 * @param signatureAlgorithm    Algoritmo para la firma
	 * @param keyEntry              Clave para firmar.
	 * @return                      Firma de los atributos.
	 * @throws es.map.es.map.afirma.exceptions.AOException
	 */
	private ASN1OctetString firma (String signatureAlgorithm, PrivateKeyEntry keyEntry) throws AOException{

		Signature sig = null;
		try {
			sig = Signature.getInstance(signatureAlgorithm);
		} catch (final Throwable e) {
			throw new AOException(
				"Error obteniendo la clase de firma para el algoritmo " + signatureAlgorithm, e
			);
		}

		byte[] tmp= null;

		try {
			tmp = signedAttr2.getEncoded(ASN1Encodable.DER);
		} catch (IOException ex) {
			Logger.getLogger(GenSignedData.class.getName()).log(Level.SEVERE, null, ex);
		}

		//Indicar clave privada para la firma
		try {
			sig.initSign(keyEntry.getPrivateKey());
		} 
		catch (final Throwable e) {
			throw new AOException(
				"Error al inicializar la firma con la clave privada", e
			);
		}



		// Actualizamos la configuracion de firma
		try {
			sig.update(tmp);
		} catch (SignatureException e) {
			throw new AOException(
					"Error al configurar la informacion de firma", e);
		}


		//firmamos.
		byte[] realSig=null;
		try {
			realSig = sig.sign();
		} 
		catch (final Throwable e) {
			throw new AOException("Error durante el proceso de firma", e);
		}

		ASN1OctetString encDigest = new DEROctetString(realSig);

		return encDigest;


	}

	/**
	 * M&eacute;todo que inserta remitentes en el "OriginatorInfo" de un sobre de tipo AuthenticatedEnvelopedData.
	 *
	 * @param data 	fichero que tiene la firma.
	 * @param parameters 
	 * @param keyEntry 
	 * @param dataType 
	 * @param policy 
	 * @param qualifier 
	 * @param signingCertificateV2 
	 * @return  La nueva firma AuthenticatedEnvelopedData con los remitentes que ten&iacute;a (si los tuviera) 
	 * 		 con la cadena de certificados nueva.
	 */
	public byte[] addOriginatorInfo(InputStream data, P7ContentSignerParameters parameters, PrivateKeyEntry keyEntry, Oid dataType,  String policy, Oid qualifier, boolean signingCertificateV2){
		//boolean isValid = false;
		byte[] retorno = null;
		try {
			ASN1InputStream is = new ASN1InputStream(data);
			// LEEMOS EL FICHERO QUE NOS INTRODUCEN
			ASN1Sequence dsq = (ASN1Sequence)is.readObject();
			Enumeration<?> e = dsq.getObjects();
			// Elementos que contienen los elementos OID Data
			DERObjectIdentifier doi = (DERObjectIdentifier)e.nextElement();
			if (doi.equals(PKCSObjectIdentifiers.signedAndEnvelopedData)){
				// Contenido de Data
				ASN1TaggedObject doj =(ASN1TaggedObject) e.nextElement();

				SignedAndEnvelopedData signEnv =new SignedAndEnvelopedData((ASN1Sequence)doj.getObject());

				//Obtenemos los originatorInfo
				ASN1EncodableVector signerInfos = new ASN1EncodableVector();
				Enumeration<?>  signers = signEnv.getSignerInfos().getObjects();
				while(signers.hasMoreElements()){
					signerInfos.add((ASN1Sequence)signers.nextElement());
				}

				//certificado del remitente
				X509Certificate[] signerCertificateChain = parameters.getSignerCertificateChain();

				ASN1EncodableVector signCerts = new ASN1EncodableVector();

				//Si no hay certificados, se deja como esta.
				if (signerCertificateChain.length != 0) {

					// algoritmo
					String signatureAlgorithm = null;
					String digestAlgorithm = null;
					AlgorithmId digestAlgorithmId = null;
					ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
					String keyAlgorithm = null;
					
					try {
						signatureAlgorithm = parameters.getSignatureAlgorithm();
						digestAlgorithm = AOCryptoUtil.getDigestAlgorithmName(signatureAlgorithm);
						digestAlgorithmId = AlgorithmId.get(digestAlgorithm);
						keyAlgorithm = Utils.getKeyAlgorithm(signatureAlgorithm, digestAlgorithmId);
						
						AlgorithmIdentifier digAlgId = makeAlgId(digestAlgorithmId.getOID().toString(), digestAlgorithmId.getEncodedParams());
						digestAlgs.add(digAlgId);
					}
					catch (final Throwable e2) {
						throw new IOException("Error de codificacion: " + e2);
					}

					TBSCertificateStructure tbs2 = TBSCertificateStructure.getInstance(ASN1Object.fromByteArray(signerCertificateChain[0].getTBSCertificate()));

					IssuerAndSerialNumber encSid = new IssuerAndSerialNumber(tbs2.getIssuer(), tbs2.getSerialNumber().getValue());

					SignerIdentifier identifier = new SignerIdentifier(encSid);

					//AlgorithmIdentifier
					AlgorithmIdentifier digAlgId = new AlgorithmIdentifier(new DERObjectIdentifier(digestAlgorithmId.getOID().toString()), new DERNull());

					//// ATRIBUTOS
					ASN1EncodableVector contextExpecific = Utils.generateSignerInfo(
		        			signerCertificateChain[0],
		        			digestAlgorithmId,
		        			digestAlgorithm,
		        			digAlgId,
		        			parameters.getContent(),
		        			policy,
		        			qualifier,
		        			signingCertificateV2,
		        			dataType,
		        			null
		        	);
		        	signedAttr2 = getAttributeSet(new AttributeTable(contextExpecific));
		        	ASN1Set signedAttr = getAttributeSet(new AttributeTable(contextExpecific));

					ASN1Set unSignedAttr = null;

					//digEncryptionAlgorithm
					AlgorithmId digestAlgorithmIdEnc = AlgorithmId.get(keyAlgorithm);
					SignerInfo nuevoSigner = Utils.signAndEnvelope(keyEntry, signatureAlgorithm, digAlgId,
							identifier, signedAttr, unSignedAttr,
							digestAlgorithmIdEnc, signedAttr2);

					//introducimos el nuevo Signer
					signerInfos.add(nuevoSigner);

					// LISTA DE CERTIFICADOS: obtenemos la lista de certificados
					signCerts = Utils.loadCertificatesList(signEnv, signerCertificateChain);
				}
				else{
					Logger.getLogger("es.gob.afirma").warning("No se ha podido obtener el certificado del nuevo firmante ");
				}


				ASN1Set certrevlist = null;

				// Se crea un nuevo AuthenticatedEnvelopedData a partir de los datos anteriores con los nuevos originantes.
				retorno = new ContentInfo(
						PKCSObjectIdentifiers.signedAndEnvelopedData,
						new SignedAndEnvelopedData(
								signEnv.getRecipientInfos(),//new DERSet(recipientInfos),
								signEnv.getDigestAlgorithms(),//new DERSet(digestAlgs),
								signEnv.getEncryptedContentInfo(),//encInfo,
								new DERSet(signCerts),//certificates,
								certrevlist,//certrevlist,
								new DERSet(signerInfos)
						)
				).getDEREncoded();
			}

		} catch (Exception ex) {
			Logger.getLogger("es.gob.afirma").severe("Error durante el proceso de insercion: " + ex);

		}
		return retorno;
	}
}

