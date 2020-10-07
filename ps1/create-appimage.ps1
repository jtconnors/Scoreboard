
####################
#
# All Scripts should have this preamble     
#
Set-variable -Name CMDLINE_ARGS -Value $args

#
# Move to the directory containing this script so we can source the env.ps1
# properties that follow
#
$STARTDIR = pwd | Select-Object | %{$_.ProviderPath}
cd $PSScriptRoot

#
# Common properties shared by scripts
#
. .\env.ps1
if ($Global:JUST_EXIT -eq "true") {
    cd $STARTDIR
    Exit 1
}
#
# End preamble
#
####################

#
# Have to manually specify main class via jar --update until
# maven-jar-plugin 3.1.2+ is released
#
Set-Variable -Name JAR_ARGS -Value @(
    '--main-class',
    """$MAINCLASS""",
    '--update',
    '--file',
    """$TARGET\$MAINJAR"""
)
Exec-Cmd("jar.exe", $JAR_ARGS)

#
# Run the jpackage command
#
Set-Variable -Name JPACKAGE_ARGS -Value @(
    '--type',
    'app-image',
    '--vendor',
    """$VENDOR_STRING""",
    '--name',
    """$LAUNCHER""",
    '--module',
    """$MAINMODULE/$MAINCLASS""",
    '--runtime-image',
    """$IMAGE"""
)
if ($VERBOSE_OPTION -ne $null) {
   $JPACKAGE_ARGS += '--verbose'
}
Exec-Cmd("$JPACKAGE_HOME\bin\jpackage.exe", $JPACKAGE_ARGS)

#
# Return to the original directory
#
cd $STARTDIR
