#!/usr/bin/env fish
set cwd "$PWD"
cd ../HexTweaks
hexcasting_root=../HexMod/ python3 doc/collate_data.py common/src/main/resources common/src/main/java hextweaks thetweakedbook doc/template.html doc/docs.html doc/data.json
cd "$cwd"
mv ../HexTweaks/doc/docs.html index.html
git add -A
git commit -m "updated docs"
git push
