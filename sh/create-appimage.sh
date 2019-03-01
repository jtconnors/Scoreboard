#!/bin/bash

#
# Move to the directory containing this script so we can source the env.sh
# properties that follow
#
cd `dirname $0`

#
# Common properties shared by scripts
#
. env.sh

#
# Have to manually specify main class via jar --update until
# maven-jar-plugin 3.1.2+ is released
if [ ! -f $TARGET/$MAINJAR ] ; then
	echo "Did you run 'mvn package' first?"
	exit 1
fi
exec_cmd "jar --main-class $MAINCLASS --update --file $TARGET/$MAINJAR"

exec_cmd "$JPACKAGE_HOME/bin/jpackage create-image $VERBOSE_OPTION --runtime-image $IMAGE --input $TARGET --output $APPIMAGE --name $LAUNCHER --main-jar $MAINJAR"
