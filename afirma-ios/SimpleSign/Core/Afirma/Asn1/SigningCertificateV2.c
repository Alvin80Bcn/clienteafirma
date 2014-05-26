/*
 * Generated by asn1c-0.9.22 (http://lionet.info/asn1c)
 * From ASN.1 module "SIGNEDDATA"
 * 	found in "SIGNEDDATA.asn1"
 * 	`asn1c -S/skeletons`
 */

#include "asn_internal.h"

#include "SigningCertificateV2.h"

static asn_TYPE_member_t asn_MBR_certs_2[] = {
	{ ATF_POINTER, 0, 0,
		(ASN_TAG_CLASS_UNIVERSAL | (16 << 2)),
		0,
		&asn_DEF_ESSCertIDv2,
		0,	/* Defer constraints checking to the member type */
		0,	/* PER is not compiled, use -gen-PER */
		0,
		""
		},
};
static ber_tlv_tag_t asn_DEF_certs_tags_2[] = {
	(ASN_TAG_CLASS_UNIVERSAL | (16 << 2))
};
static asn_SET_OF_specifics_t asn_SPC_certs_specs_2 = {
	sizeof(struct certs),
	offsetof(struct certs, _asn_ctx),
	0,	/* XER encoding is XMLDelimitedItemList */
};
static /* Use -fall-defs-global to expose */
asn_TYPE_descriptor_t asn_DEF_certs_2 = {
	"certs",
	"certs",
	SEQUENCE_OF_free,
	SEQUENCE_OF_print,
	SEQUENCE_OF_constraint,
	SEQUENCE_OF_decode_ber,
	SEQUENCE_OF_encode_der,
	SEQUENCE_OF_decode_xer,
	SEQUENCE_OF_encode_xer,
	0, 0,	/* No PER support, use "-gen-PER" to enable */
	0,	/* Use generic outmost tag fetcher */
	asn_DEF_certs_tags_2,
	sizeof(asn_DEF_certs_tags_2)
		/sizeof(asn_DEF_certs_tags_2[0]), /* 1 */
	asn_DEF_certs_tags_2,	/* Same as above */
	sizeof(asn_DEF_certs_tags_2)
		/sizeof(asn_DEF_certs_tags_2[0]), /* 1 */
	0,	/* No PER visible constraints */
	asn_MBR_certs_2,
	1,	/* Single element */
	&asn_SPC_certs_specs_2	/* Additional specs */
};

static asn_TYPE_member_t asn_MBR_policies_4[] = {
	{ ATF_POINTER, 0, 0,
		(ASN_TAG_CLASS_UNIVERSAL | (16 << 2)),
		0,
		&asn_DEF_PolicyInformation,
		0,	/* Defer constraints checking to the member type */
		0,	/* PER is not compiled, use -gen-PER */
		0,
		""
		},
};
static ber_tlv_tag_t asn_DEF_policies_tags_4[] = {
	(ASN_TAG_CLASS_UNIVERSAL | (16 << 2))
};
static asn_SET_OF_specifics_t asn_SPC_policies_specs_4 = {
	sizeof(struct policies),
	offsetof(struct policies, _asn_ctx),
	0,	/* XER encoding is XMLDelimitedItemList */
};
static /* Use -fall-defs-global to expose */
asn_TYPE_descriptor_t asn_DEF_policies_4 = {
	"policies",
	"policies",
	SEQUENCE_OF_free,
	SEQUENCE_OF_print,
	SEQUENCE_OF_constraint,
	SEQUENCE_OF_decode_ber,
	SEQUENCE_OF_encode_der,
	SEQUENCE_OF_decode_xer,
	SEQUENCE_OF_encode_xer,
	0, 0,	/* No PER support, use "-gen-PER" to enable */
	0,	/* Use generic outmost tag fetcher */
	asn_DEF_policies_tags_4,
	sizeof(asn_DEF_policies_tags_4)
		/sizeof(asn_DEF_policies_tags_4[0]), /* 1 */
	asn_DEF_policies_tags_4,	/* Same as above */
	sizeof(asn_DEF_policies_tags_4)
		/sizeof(asn_DEF_policies_tags_4[0]), /* 1 */
	0,	/* No PER visible constraints */
	asn_MBR_policies_4,
	1,	/* Single element */
	&asn_SPC_policies_specs_4	/* Additional specs */
};

static asn_TYPE_member_t asn_MBR_SigningCertificateV2_1[] = {
	{ ATF_NOFLAGS, 0, offsetof(struct SigningCertificateV2, certs),
		(ASN_TAG_CLASS_UNIVERSAL | (16 << 2)),
		0,
		&asn_DEF_certs_2,
		0,	/* Defer constraints checking to the member type */
		0,	/* PER is not compiled, use -gen-PER */
		0,
		"certs"
		},
	{ ATF_POINTER, 1, offsetof(struct SigningCertificateV2, policies),
		(ASN_TAG_CLASS_UNIVERSAL | (16 << 2)),
		0,
		&asn_DEF_policies_4,
		0,	/* Defer constraints checking to the member type */
		0,	/* PER is not compiled, use -gen-PER */
		0,
		"policies"
		},
};
static ber_tlv_tag_t asn_DEF_SigningCertificateV2_tags_1[] = {
	(ASN_TAG_CLASS_UNIVERSAL | (16 << 2))
};
static asn_TYPE_tag2member_t asn_MAP_SigningCertificateV2_tag2el_1[] = {
    { (ASN_TAG_CLASS_UNIVERSAL | (16 << 2)), 0, 0, 1 }, /* certs at 131 */
    { (ASN_TAG_CLASS_UNIVERSAL | (16 << 2)), 1, -1, 0 } /* policies at 132 */
};
static asn_SEQUENCE_specifics_t asn_SPC_SigningCertificateV2_specs_1 = {
	sizeof(struct SigningCertificateV2),
	offsetof(struct SigningCertificateV2, _asn_ctx),
	asn_MAP_SigningCertificateV2_tag2el_1,
	2,	/* Count of tags in the map */
	0, 0, 0,	/* Optional elements (not needed) */
	-1,	/* Start extensions */
	-1	/* Stop extensions */
};
asn_TYPE_descriptor_t asn_DEF_SigningCertificateV2 = {
	"SigningCertificateV2",
	"SigningCertificateV2",
	SEQUENCE_free,
	SEQUENCE_print,
	SEQUENCE_constraint,
	SEQUENCE_decode_ber,
	SEQUENCE_encode_der,
	SEQUENCE_decode_xer,
	SEQUENCE_encode_xer,
	0, 0,	/* No PER support, use "-gen-PER" to enable */
	0,	/* Use generic outmost tag fetcher */
	asn_DEF_SigningCertificateV2_tags_1,
	sizeof(asn_DEF_SigningCertificateV2_tags_1)
		/sizeof(asn_DEF_SigningCertificateV2_tags_1[0]), /* 1 */
	asn_DEF_SigningCertificateV2_tags_1,	/* Same as above */
	sizeof(asn_DEF_SigningCertificateV2_tags_1)
		/sizeof(asn_DEF_SigningCertificateV2_tags_1[0]), /* 1 */
	0,	/* No PER visible constraints */
	asn_MBR_SigningCertificateV2_1,
	2,	/* Elements count */
	&asn_SPC_SigningCertificateV2_specs_1	/* Additional specs */
};

