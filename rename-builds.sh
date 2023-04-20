#!/usr/bin/env fish
for f in (find */build/libs/ -name "*.jar" | grep -v "sources" | grep -v "shadow" | grep -v transform | grep -v common)
    set bname (basename "$f" .jar)
    set bplat (echo "$f" | awk -F/ '{print $1}')
    mv -v "$f" builds/"$bname-$bplat.jar"
end