# How to Update This Skill

The skill was last generated from commit `8647d610a91383b2b95813737cf6a8eaefb5d71a`.
The baseline commit hash is stored in [`COMMIT`](COMMIT).

## When to update

Update the skill when the elastic4s source code has changed in ways that affect the public DSL:
new aggregation types, new query options, renamed methods, new client backends, etc.

## Process

### 1. Identify what changed

```bash
# List all files that changed between the baseline and now:
git diff 8647d610a91383b2b95813737cf6a8eaefb5d71a --name-only

# Focus on files relevant to the skill:
git diff 8647d610a91383b2b95813737cf6a8eaefb5d71a -- \
  elastic4s-core/src/main/scala/com/sksamuel/elastic4s/api/ \
  elastic4s-domain/src/main/scala/com/sksamuel/elastic4s/requests/searches/ \
  elastic4s-core/src/main/scala/com/sksamuel/elastic4s/requests/searches/aggs/ \
  elastic4s-domain/src/main/scala/com/sksamuel/elastic4s/requests/searches/aggs/
```

### 2. Map changed files to skill files

| Source path | Skill file to update |
|---|---|
| `api/QueryApi.scala` or `queries/**` | `queries.md` |
| `api/AggregationApi.scala` or `aggs/**` | `aggregations.md` |
| `api/SearchApi.scala` or `SearchRequest.scala` | `search-request.md` |
| `api/ScrollApi.scala`, `api/PitApi.scala`, `SearchIterator.scala` | `pagination.md` |
| `SearchResponse.scala`, `SearchHit.scala`, `HitReader.scala`, `aggs/responses/**` | `results.md` |
| `ElasticClient.scala`, `ElasticProperties.scala`, client backends | `client.md` |

### 3. Update each affected skill file

Read the diff for the relevant source files, then edit the corresponding skill file to reflect:
- New methods or parameters added
- Methods renamed or removed
- New types introduced (new aggregation, new query variant, etc.)
- Behaviour changes worth documenting

Also update `examples.md` if a new feature deserves a concrete usage example.

### 4. Update the baseline commit

After updating the skill files, record the new commit hash:

```bash
git rev-parse HEAD > .claude/skills/elastic4s/COMMIT
```

Then commit the updated skill alongside any other changes.
