#!/bin/sh
#------------------------------------------------------
# Script to push language files to Transifex
#
# Laurent Destailleur (eldy) - eldy@users.sourceforge.net
#------------------------------------------------------
# Usage: txpush.sh (source|xx_XX) [-r dolibarr.file] [-f]
#------------------------------------------------------

export project='dolidroid'

# Syntax
if [ "x$1" = "x" ]
then
	echo "This push local files to transifex for project $project."
	echo "Note:  If you push a language file (not source), file will be skipped if transifex file is newer."
	echo "       Using -f will overwrite translation but not memory."
	echo "Usage: ./dev/translation/txpush.sh (source|xx|all) [-r ".$project.".file] [-f] [--no-interactive]"
	exit
fi

if [ ! -d ".tx" ]
then
	echo "Script must be ran from root directory of project with command ./dev/translation/txpush.sh"
	exit
fi

if [ "x$1" = "xsource" ]
then
	echo "tx push -s $2 $3"
	tx push -s $2 $3 
else
    if [ "x$1" = "xall" ]
    then
		for dir in `find app/src/main/res/values-* -type d`
		do
			shortdir=`basename $dir | sed -s s/values\-//g`
			file=$3
			echo $file
			export basefile=`basename $file | sed -s s/values\-//g`
			echo "tx push --skip -t -l $shortdir $2 $3 $4"
			tx push --skip -t -l $shortdir $2 $3 $4
		done
    else
		for file in `find app/src/main/res/values-$1/strings.xml -type f`
		do
			echo $file
			export basefile=`basename $file | sed -s s/\.xml//g`
			echo "tx push --skip -r $project.$basefile -t -l $1 $2 $3 $4"
			tx push --skip -r $project.$basefile -t -l $1 $2 $3 $4
		done
	fi
fi
