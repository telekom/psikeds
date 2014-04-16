TOPDIR=$(HOME)
# ---
PSIKEDSREP=$(TOPDIR)/gits.remote.net/psikeds
PKBEDITOR=$(PSIKEDSREP)/KnowledgeBaseEditor/src/psiKedsXtxKrl

PKBXTEMAT=$(PKBEDITOR)/xte.mat
PKBREFMAT=$(PKBEDITOR)/pkb.mat

PKBXTXKRL=PsiKedsXtxKrl
PKBXTXDEF=$(PKBXTXKRL).xtext
PKBXTXGEN=$(PKBXTXKRL)Generator.xtend
# ----
EWSXTX=$(TOPDIR)/ews.xtx
EWSXTXPKB=$(EWSXTX)/org.psikeds.kb
EWSXTXPKBDEFDIR=$(EWSXTXPKB)/src/org/psikeds/kb
EWSXTXPKBCGENDIR=$(EWSXTXPKBDEFDIR)/modelling/generator

EWSPKB=$(TOPDIR)/ews.pkb/psikedsLetterEnvelopePostDemo
EWSPKBSRC=$(EWSPKB)/src
EWSPKBSRCGEN=$(EWSPKB)/src-gen

PKBREF=psiKedsLetterEnvelope
PKBREFPKB=$(PKBREF).pkb
PKBREFXML=$(PKBREFPKB).xml

PKBTPLPKB=psikedsTemplate.pkb

# ---
SRC_PKBDEFFILE=$(EWSXTXPKBDEFDIR)/$(PKBXTXDEF)
SRC_PKBCGENFILE=$(EWSXTXPKBCGENDIR)/$(PKBXTXGEN)
SRC_PKBREFPKBFILE=$(EWSPKBSRC)/$(PKBREFPKB)
SRC_PKBREFXMLFILE=$(EWSPKBSRCGEN)/$(PKBREFXML)
SRC_PKBTPLPKBFILE=$(EWSPKBSRC)/$(PKBTPLPKB)

TGT_PKBDEFFILE=$(PKBXTEMAT)/$(PKBXTXDEF)
TGT_PKBCGENFILE=$(PKBXTEMAT)/$(PKBXTXGEN)
TGT_PKBREFPKBFILE=$(PKBREFMAT)/$(PKBREFPKB)
TGT_PKBREFXMLFILE=$(PKBREFMAT)/$(PKBREFXML)
TGT_PKBTPLPKBFILE=$(PKBREFMAT)/$(PKBTPLPKB)

SAVEXT=sav

use:
	echo "make pull => get xtexts, xtends, and reference database into git repository"
	echo "make push => pushes xtexts, xtends, and reference database into eclipse directories"

pull:	pull2git

savsav:
	echo "saving saved files"
	find . -name "*.$(SAVEXT)" | while read file; do echo "saving $$file"; mv $$file $$file.$(SAVEXT); done

sav:	savsav
	echo "saving existing target files"
	cp $(TGT_PKBDEFFILE) $(TGT_PKBDEFFILE).$(SAVEXT)
	cp $(TGT_PKBCGENFILE) $(TGT_PKBREFPKBFILE).$(SAVEXT)
	cp $(TGT_PKBREFXMLFILE) $(TGT_PKBREFXMLFILE).$(SAVEXT)
	cp $(TGT_PKBREFXMLFILE) $(TGT_PKBREFXMLFILE).$(SAVEXT)
	cp $(TGT_PKBTPLPKBFILE) $(TGT_PKBTPLPKBFILE).$(SAVEXT)

pull2git:	sav	
	echo "1) cp:XTX.DEF $(SRC_PKBDEFFILE) => $(TGT_PKBDEFFILE)"
	cp $(SRC_PKBDEFFILE) $(TGT_PKBDEFFILE)
	echo "2) cp:XTX.CGEN $(SRC_PKBCGENFILE) => $(TGT_PKBCGENFILE)"
	cp $(SRC_PKBCGENFILE) $(TGT_PKBCGENFILE)
	echo "3) cp:PKB.REF $(SRC_PKBREFPKBFILE) => $(TGT_PKBREFPKBFILE)"
	cp $(SRC_PKBREFPKBFILE) $(TGT_PKBREFPKBFILE)
	echo "4) cp:PKB.XML $(SRC_PKBREFXMLFILE) => $(TGT_PKBREFXMLFILE)"
	cp $(SRC_PKBREFXMLFILE) $(TGT_PKBREFXMLFILE)
	echo "5) cp:PKB.TPL $(SRC_PKBTPLPKBFILE) => $(TGT_PKBTPLPKBFILE)"
	cp $(SRC_PKBTPLPKBFILE) $(TGT_PKBTPLPKBFILE)
 
clean:
	find . -name "*.$(SAVEXT)" | while read file; do rm $$file; done

