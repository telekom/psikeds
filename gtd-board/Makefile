# (c) Karsten Reincke GTD - rewrite

etc: erase-task-carcasses

erase-task-carcasses:
	cp gtd-free-data.xml gtd-free-data.xml.sav
	cat gtd-free-data.xml.sav | grep -v DELETED > gtd-free-data.xml

clean:
	rm *.lock
	rm log/*.*
	rm gtd-free-*backup*
	
