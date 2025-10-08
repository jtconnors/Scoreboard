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

exec_cmd "java --enable-native-access=javafx.graphics --enable-native-access=javafx.media --module-path $MODPATH --module $MAINMODULE"
