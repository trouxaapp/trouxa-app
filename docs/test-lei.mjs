// Parity check against the Kotlin LeiExplicada.
// 1. ./gradlew :app:testDebugUnitTest -Pementas=<file.tsv>   (writes explicadas.txt next to it)
// 2. node site/test-lei.mjs <file.tsv>                        (writes explicadas-js.txt and diffs)
import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { explicar } from "./lei.js";

const tsv = process.argv[2];
if (!tsv) {
  console.error("usage: node site/test-lei.mjs <ementas.tsv>");
  process.exit(2);
}
let out = "";
let vazias = 0;
for (const linha of readFileSync(tsv, "utf8").split(/\r?\n/).filter(Boolean)) {
  const i = linha.indexOf("\t");
  const tipo = linha.slice(0, i);
  const ementa = linha.slice(i + 1);
  const e = explicar(tipo, ementa);
  if (!e.emUmaFrase) vazias++;
  out += `[${tipo}] ${ementa.slice(0, 150)}\n  FRASE: ${e.emUmaFrase}\n  OBJETIVO: ${e.objetivo}\n  TERMOS: ${e.termos.map((t) => t.termo).join(", ")}\n\n`;
}
out += `sem frase: ${vazias}`;
writeFileSync(join(dirname(tsv), "explicadas-js.txt"), out);

const kt = readFileSync(join(dirname(tsv), "explicadas.txt"), "utf8").split("\n");
const js = out.split("\n");
const diffs = kt.map((l, i) => [i, l, js[i]]).filter(([, a, b]) => a !== b);
for (const [i, a, b] of diffs.slice(0, 20)) console.log(`line ${i + 1}\n  kt: ${a}\n  js: ${b}`);
console.log(`${diffs.length} different lines out of ${kt.length}`);
process.exit(diffs.length ? 1 : 0);
