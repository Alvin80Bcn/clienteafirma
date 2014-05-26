/*
 * Generated by asn1c-0.9.22 (http://lionet.info/asn1c)
 * From ASN.1 module "SIGNEDDATA"
 * 	found in "SIGNEDDATA.asn1"
 * 	`asn1c -S/skeletons`
 */

#ifndef	_SPUserNotice_H_
#define	_SPUserNotice_H_


#include "asn_application.h"

/* Including external dependencies */
#include "constr_SEQUENCE.h"

#ifdef __cplusplus
extern "C" {
#endif

/* Forward declarations */
struct NoticeReference;
struct DisplayText;

/* SPUserNotice */
typedef struct SPUserNotice {
	struct NoticeReference	*noticeRef	/* OPTIONAL */;
	struct DisplayText	*explicitText	/* OPTIONAL */;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} SPUserNotice_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_SPUserNotice;

#ifdef __cplusplus
}
#endif

/* Referred external types */
#include "NoticeReference.h"
#include "DisplayText.h"

#endif	/* _SPUserNotice_H_ */
