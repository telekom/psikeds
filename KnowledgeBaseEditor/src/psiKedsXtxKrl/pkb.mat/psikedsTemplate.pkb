KB-META:
01: id psiKedsTemplate
02: name "psiKeds template knowledge base"
// teaser "kbTeaser"
04: release "0.1.1"
05: (C) "2014, Karsten Reincke, Deutsche Telekom AG, Darmstadt"

07: created 2014-04-16
08: modified 2014-04-16
09: language "kbLanguage"
10: engineer "kbCreatorName"
11: description "kbDescription"

PSIKEDS-SENSORS:

PSIKEDS-CONCEPTS:

PSIKEDS-PURPOSES: 
purpose ps ( label "ps" )

PSIKEDS-VARIANTS:
variant pv ( singleton true label "pv" )


PSIKEDS-IS-FULFILLED-BY-STATEMENTS:
purpose.system ps ( isFulfilledBy { *pv> pv } )

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:

PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:

PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:

PSIKEDS-INFERENCE-CONDITIONS:




