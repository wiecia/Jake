VERSION="1.0-SNAPSHOT"
SHELL="bash"
MVN=mvn ${MVNEXTRAARGS}

all: gui

start: gui2
	cd gui; ${MVN} exec:java

gui2: 
	mvn -Dmaven.test.skip=true install


quickstart: gui
	cd gui; ${MVN} exec:java -Dexec.args=${PROJECTFOLDER}

gui: core
	[[ -e .rebuild_$@ ]] && { cd $@; ${MVN} package install; } || true
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || ${MVN} package install || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q && ${MVN} package install || true
	rm -f .rebuild_gui

core: fss ics ics-xmpp
	[[ -e .rebuild_$@ ]] && { cd $@; ${MVN} package install; } || true
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { ${MVN} package install; touch ../.rebuild_gui; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { ${MVN} package install; touch ../.rebuild_gui; } || true
	rm -f .rebuild_$@

fss:
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { ${MVN} package install; touch ../.rebuild_core; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { ${MVN} package install; touch ../.rebuild_core; } || true
ics:
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { ${MVN} package install; touch ../.rebuild_ics-xmpp; touch ../.rebuild_core; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { ${MVN} package install; touch ../.rebuild_core; } || true

ics-xmpp: ics
	[[ -e .rebuild_$@ ]] && { cd $@; ${MVN} package install; } || true
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { ${MVN} package install; touch ../.rebuild_core; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { ${MVN} package install; touch ../.rebuild_core; } || true
	rm -f .rebuild_$@

clean:
	${MVN} clean

mrproper: clean
	rm -rf ~/.m2/repository/com/{jakeapp,doublesignal}/
	rm -f target/*-${VERSION}.jar

lazyclean:
	cp fss/target/fss-${VERSION}.jar .backup.fss-${VERSION}.jar
	${MVN} clean
	mkdir fss/target/
	mv .backup.fss-${VERSION}.jar fss/target/fss-${VERSION}.jar

up:
	oldrev=$$(svn info |grep '^Revision: '|sed 's/Revision: //g'); svn up; newrev=$$(svn info |grep '^Revision: '|sed 's/Revision: //g'); [ "$$oldrev" == "$$newrev" ] || svn log -v -r$$oldrev:$$newrev|while read line; do echo "$$line"; sleep 0.3; echo "$$line"|grep -q -- "-----" && sleep 3; done

jar:
	${MVN} clean
	${MVN} package
	cd releases; rm -rf temp; mkdir -p temp 
	cd releases/temp; unzip ../../gui/target/gui-${VERSION}.one-jar.jar && cp -v ../../{core,ics,fss}/target/*-${VERSION}.jar main/ && rm -f ../jake-current.jar && zip -9 -r ../jake-current.jar * 
	cd releases; rm -rf temp;
	@echo release ready under releases/jake-current.jar
	@echo run with java -jar releases/jake-current.jar

.PHONY: gui core fss ics ics-xmpp start quickstart
