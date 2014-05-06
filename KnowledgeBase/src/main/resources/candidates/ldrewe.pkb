KB-META: 
01: id ldrewe
02: name "Linux-Day-Rewe-Demo-Knowledgebase"
03: teaser "demo database for the lecture on Linux Days 2014/Berlin"
04: release "0.90"
05: (C) "Karsten Reincke, Deutsche Telekom AG, Darmstadt 2014"
06: licensing "CC-BY-SA-4.0"
07: created 2014-05-05
08: modified 2014-05-05
10: engineer "Karsten Reincke"


PSIKEDS-SENSORS:
PSIKEDS-CONCEPTS:

PSIKEDS-PURPOSES:

purpose relishDinner ( 
	root true
	label "relish.Dinner"
	)

purpose addTaste (
	root false
	label "add:Taste"
)

purpose addVitamins (
	root false
	label "add:Vitamins"
)

purpose addProteins (
	root false
	label "add:Proteins"
)

purpose addCarbohydrats (
	root false
	label "add:Carbohydrats"
)

PSIKEDS-VARIANTS:

variant VeggiMeal (
	label "Veggi Meal"
	type explicit
)

variant CarniMeal (
	label "Carni Meal"
	type explicit
)

variant Hollandaise(
	label "Sauce Hollandaise"
	type explicit
)

variant BratenSosse(
	label "Bratensoße"
	type explicit
)

variant Spargel (
	label "Spargel"
	type explicit
)

variant Erbsen (
	label "Erbsen"
	type explicit
)
variant Schinken (
	label "Schinken"
	type explicit
)
variant Braten (
	label "Braten"
	type explicit
)
variant Kartoffeln (
	label "Kartoffeln"
	type explicit
)
variant Nudeln (
	label "Nudeln"
	type explicit
)

PSIKEDS-IS-FULFILLED-BY-STATEMENTS:
purpose.system relishDinner (
	isFulfilledBy {
		*pv> VeggiMeal
		| *pv> CarniMeal
		}
)

purpose.system addTaste (
	isFulfilledBy {
		*pv> Hollandaise
		| *pv> BratenSosse
	}
)

purpose.system addVitamins (
	isFulfilledBy {
		*pv> Spargel
		| *pv> Erbsen
	}
)

purpose.system addProteins ( 
	isFulfilledBy {
		*pv> Braten
		| *pv> Schinken
	}
)

purpose.system addCarbohydrats (
	isFulfilledBy {
		*pv> Kartoffeln
		| *pv> Nudeln
	}
)

PSIKEDS-IS-CONSTITUTED-BY-STATEMENTS:
purpose.variant VeggiMeal (
	isConstitutedBy {
		< 1 of *ps> addTaste >
		& < 1 of *ps> addVitamins >
		& < 1 of *ps> addCarbohydrats >
	}
)


purpose.variant CarniMeal (
	isConstitutedBy {
		< 1 of *ps> addTaste >
		& < 1 of *ps> addVitamins >
		& < 1 of *ps> addProteins >
		& < 1 of *ps> addCarbohydrats >
	}
)


PSIKEDS-LOGICAL-INFERENCE-ELEMENTS:
event.var VeggiMealWithHollandaise (
	label "Veggi Meal mit Hollandaise"
	context [ *pv> VeggiMeal *ps> addTaste ]
	fact *pv> Hollandaise
)


event.var VeggiMealWithSpargel (
	label "Veggi Meal mit Spargel"
	context [ *pv> VeggiMeal *ps> addVitamins ]
	fact *pv> Spargel
)

event.var CarniMealWithHollandaise (
	label "Carni Meal mit Hollandaise"
	context [ *pv> CarniMeal *ps> addTaste ]
	fact *pv> Hollandaise
)

event.var CarniMealWithSpargel (
	label "Carni Meal mit Spargel"
	context [ *pv> CarniMeal *ps> addVitamins ]
	fact *pv> Spargel
)

event.var CarniMealWithSchinken (
	label "Carni Meal mit Schinken"
	context [ *pv> CarniMeal *ps> addProteins ]
	fact *pv> Schinken
)


event.var CarniMealWithKartoffeln (
	label "Carni Meal mit Kartoffeln"
	context [ *pv> CarniMeal *ps> addCarbohydrats ]
	fact *pv> Kartoffeln
)

event.var VeggiMealWithKartoffeln (
	label "VeggiMeal mit Kartoffeln"
	context [ *pv> VeggiMeal *ps> addCarbohydrats ]
	fact *pv> Kartoffeln
)

PSIKEDS-RELATIONAL-INFERENCE-ELEMENTS:
PSIKEDS-INFERENCE-CONDITIONS:

/* R-1.1.1: Wenn VeggiMeal mit Hollandaise, dann auch Spargel */
logic.rule IFVMwithHoolandaiseTHENVMwithSpargel (
	label "IF VM mit Hoolandaise THEN VM mit Spargel"
	nexus *pv> VeggiMeal
	induces [ 
		{ *ev> VeggiMealWithHollandaise }
		=>  *ev> VeggiMealWithSpargel ]
)

/* R.1.1.2: Wenn VeggiMeal mit Spargel, dann auch Hollandaise */
logic.rule IFVMwithSpargelTHENVMwithHoolandaise (
	label "IF VM mit Spargel THEN VM mit Hollandaise"
	nexus *pv> VeggiMeal
	induces [ 
		{ *ev> VeggiMealWithSpargel }
		=>  *ev>  VeggiMealWithHollandaise ]
)

/* R-1.2.1: Wenn CarniMeal mit Hollandaise, dann auch Spargel */
logic.rule IFCMwithHoolandaiseTHENCMwithSpargel (
	label "IF CM mit Hoolandaise THEN CM mit Spargel"
	nexus *pv> CarniMeal
	induces [ 
		{ *ev> CarniMealWithHollandaise }
		=>  *ev> CarniMealWithSpargel ]
)

/* R.1.2.2: Wenn CarniMeal mit Spargel, dann auch Hollandaise */
logic.rule IFCMwithSpargelTHENCMwithHoolandaise (
	label "IF CM mit Spargel THEN CM mit Hollandaise"
	nexus *pv> CarniMeal
	induces [ 
		{ *ev> CarniMealWithSpargel }
		=>  *ev>  CarniMealWithHollandaise ]
)

/* R.2: Wenn CarniMeal mit Spargel, dann auch Schinken */
logic.rule IFCMwithSpargelTHENCMwithSchinken (
	label "IF CM mit Spargel THEN CM mit Schinken"
	nexus *pv> CarniMeal
	induces [ 
		{ *ev> CarniMealWithSpargel }
		=>  *ev>  CarniMealWithSchinken ]
)

/* R.3.1: Wenn CarniMeal mit Spargel, dann auch Kartoffeln */
logic.rule IFCMwithSpargelTHENCMwithKartoffeln (
	label "IF CM mit Spargel THEN CM mit Kartoffeln"
	nexus *pv> CarniMeal
	induces [ 
		{ *ev> CarniMealWithSpargel }
		=>  *ev>  CarniMealWithKartoffeln ]
)

/* R.3.2: Wenn VeggiMeal mit Spargel, dann auch Kartoffeln */
logic.rule IFVMwithSpargelTHENVMwithKartoffeln (
	label "IF VM mit Spargel THEN VM mit Kartoffeln"
	nexus *pv> VeggiMeal
	induces [ 
		{ *ev> VeggiMealWithSpargel }
		=>  *ev>  VeggiMealWithKartoffeln ]
)


