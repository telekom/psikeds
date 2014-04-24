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
purpose psRoot 
( root true 
  label "psRoot"
)
purpose ps1 ( label "ps1" )
purpose ps2 ( label "ps2" )

PSIKEDS-VARIANTS:

variant pvRoot (
  label "pvRoot"
  type explicit
  
)

variant pvComponent1 (
  label "constituting variant 1"
  type explicit
)

variant pvComponent2 (
  label "constituting variant 2"
  type explicit
)


/*
 * simple example ps-pv-(tree)-structure:
 * 
 *  psROOT is-fulfilled-by
 *  (
 *    pvRoot is-constituted-by:
 *    (
 *      ps1 is-fulfilled-by (pvComponent1 [...] )
 *      p22 is-fulfilled-by (pvComponent2 [...] )
 *      [...]
 *    )
 *    [...]
 *  )
 * 
 */

PSIKEDS-IS-FULFILLED-BY-STATEMENTS:

purpose.system psRoot ( isFulfilledBy { *pv> pvRoot } )
purpose.system ps1 ( isFulfilledBy { *pv> pvComponent1 } )
purpose.system ps2 ( isFulfilledBy { *pv> pvComponent2 } )

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:
purpose.variant pvRoot (
   isConstitutedBy {
       < 1 of *ps> ps1 >
     & < 1 of *ps> ps2 >
   }
)

 
PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:

PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:

PSIKEDS-INFERENCE-CONDITIONS: