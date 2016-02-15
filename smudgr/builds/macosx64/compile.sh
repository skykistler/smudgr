SHDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd "$SHDIR"

cp -f ../../smudgr.jar "./bin/smudgr.app/Contents/Java/"

open ./bin/smudgr.app
