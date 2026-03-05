# 🧠 Git — Complete Learning Guide

> A hands-on guide covering everything you need to know about Git, from basics to advanced workflows. Work through the tasks in order!

---

## 📚 Table of Contents

1. [What is Git?](#what-is-git)
2. [Setup & Configuration](#setup--configuration)
3. [Core Concepts](#core-concepts)
4. [Repository Basics](#repository-basics)
5. [Staging & Committing](#staging--committing)
6. [Branching](#branching)
7. [Merging & Rebasing](#merging--rebasing)
8. [Remote Repositories](#remote-repositories)
9. [Undoing Things](#undoing-things)
10. [Stashing](#stashing)
11. [Tags](#tags)
12. [Git Log & History](#git-log--history)
13. [Advanced Topics](#advanced-topics)
14. [Workflows](#workflows)
15. [Cheatsheet](#cheatsheet)

---

## 1. What is Git?

Git is a **distributed version control system** (VCS) — it tracks changes in files over time and allows multiple people to collaborate on the same project.

### Key Ideas
- Every developer has a **full copy** of the repository (distributed)
- Changes are tracked as **snapshots**, not diffs
- Almost all operations are **local** (fast!)
- Git has **integrity** — everything is checksummed (SHA-1)

### Git vs GitHub
| Git | GitHub |
|-----|--------|
| Local version control tool | Cloud hosting for Git repos |
| Runs on your machine | Website / service |
| Free, open-source | Free + paid tiers |
| No account needed | Requires account |

---

## 2. Setup & Configuration

### Install Git
```bash
# macOS
brew install git

# Ubuntu/Debian
sudo apt install git

# Windows
# Download from https://git-scm.com
```

### Configure Your Identity
```bash
git config --global user.name "Jane Doe"
git config --global user.email "jane@example.com"
```

### Set Default Editor
```bash
git config --global core.editor "code --wait"   # VS Code
git config --global core.editor "vim"            # Vim
git config --global core.editor "nano"           # Nano
```

### Set Default Branch Name
```bash
git config --global init.defaultBranch main
```

### View Your Config
```bash
git config --list
git config user.name    # view one setting
```

### ✅ Task 1 — Setup
- [ ] Install Git on your machine
- [ ] Set your `user.name` and `user.email`
- [ ] Run `git config --list` and verify your settings

---

## 3. Core Concepts

### The Three Areas
```
Working Directory  →  Staging Area (Index)  →  Repository (.git)
  (your files)          (git add)               (git commit)
```

| Area | Description |
|------|-------------|
| **Working Directory** | Files you're actively editing |
| **Staging Area** | Files prepared for the next commit |
| **Repository** | The history of all committed snapshots |

### File States
```
Untracked → Staged → Committed → Modified → Staged → ...
```

- **Untracked** — Git doesn't know about this file yet
- **Staged** — File is added and ready to be committed
- **Committed** — Saved permanently in the local database
- **Modified** — Tracked file has been changed but not staged

---

## 4. Repository Basics

### Initialize a New Repo
```bash
mkdir my-project
cd my-project
git init
# Creates a hidden .git/ folder
```

### Clone an Existing Repo
```bash
git clone https://github.com/user/repo.git
git clone https://github.com/user/repo.git my-folder   # custom folder name
```

### Check Repo Status
```bash
git status
git status -s   # short/compact output
```

### ✅ Task 2 — First Repo
- [ ] Create a new folder called `git-practice`
- [ ] Run `git init` inside it
- [ ] Create a file `hello.txt` with some content
- [ ] Run `git status` — notice the file is **untracked**

---

## 5. Staging & Committing

### Stage Files
```bash
git add hello.txt            # stage one file
git add src/                 # stage a directory
git add *.js                 # stage all .js files
git add .                    # stage all changes in current directory
git add -p                   # interactively stage chunks (patch mode)
```

### Unstage Files
```bash
git restore --staged hello.txt   # modern way (Git 2.23+)
git reset HEAD hello.txt         # older way
```

### Commit
```bash
git commit -m "Add hello.txt with greeting"
git commit                       # opens editor for multi-line message
git commit -am "Fix typo"        # stage tracked files + commit in one step
```

### What Makes a Good Commit Message?
```
Short summary (50 chars or less)

Optional longer description after a blank line.
Explain WHAT changed and WHY, not HOW.

- Use bullet points for multiple changes
- Reference issues: Fixes #42
```

### View What You've Staged
```bash
git diff --staged   # diff between staged and last commit
git diff            # diff between working dir and staged
```

### ✅ Task 3 — Stage & Commit
- [ ] Stage `hello.txt` using `git add`
- [ ] Run `git status` again — file should be **staged**
- [ ] Commit with a meaningful message
- [ ] Run `git log` to see your commit

---

## 6. Branching

Branches let you work on features or fixes in isolation without affecting the main codebase.

### Branch Commands
```bash
git branch                    # list local branches
git branch -a                 # list all branches (including remote)
git branch feature/login      # create a new branch
git switch feature/login      # switch to branch (Git 2.23+)
git checkout feature/login    # older way to switch
git switch -c feature/login   # create AND switch in one step
git branch -d feature/login   # delete branch (safe)
git branch -D feature/login   # force delete
git branch -m old-name new-name  # rename branch
```

### Example: Feature Branch Workflow
```bash
# Start from main
git switch main

# Create and switch to a new branch
git switch -c feature/user-auth

# Make changes and commit
echo "auth code" > auth.js
git add auth.js
git commit -m "Add user authentication module"

# Go back to main
git switch main
```

### Visualize Branches
```bash
git log --oneline --graph --all
```

### ✅ Task 4 — Branching
- [ ] Create a branch called `feature/about-page`
- [ ] Switch to it
- [ ] Create a file `about.txt` and commit it
- [ ] Switch back to `main` — notice `about.txt` is gone
- [ ] Run `git log --oneline --graph --all`

---

## 7. Merging & Rebasing

### Merge
Combines the history of two branches.

```bash
# Merge feature branch INTO main
git switch main
git merge feature/user-auth
```

**Types of merges:**

| Type | When | What happens |
|------|------|--------------|
| Fast-forward | No diverging commits | Pointer just moves forward |
| 3-way merge | Branches diverged | Creates a new merge commit |

```bash
git merge --no-ff feature/login   # force a merge commit even if fast-forward possible
git merge --squash feature/login  # squash all branch commits into one
```

### Resolve Merge Conflicts
When Git can't auto-merge, you get a conflict:
```
<<<<<<< HEAD
Hello from main branch
=======
Hello from feature branch
>>>>>>> feature/greeting
```

Steps to resolve:
1. Open the file and edit it to the desired state
2. Remove the conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`)
3. `git add <file>`
4. `git commit`

```bash
git merge --abort   # cancel the merge
```

### Rebase
Reapplies commits on top of another branch — creates a **linear history**.

```bash
git switch feature/login
git rebase main          # rebase current branch onto main
```

```
Before rebase:       After rebase:
A--B--C (main)       A--B--C (main)
    \                         \
     D--E (feature)            D'--E' (feature)
```

> ⚠️ **Golden Rule:** Never rebase commits that have been pushed to a shared remote branch.

### Interactive Rebase
Powerful tool to rewrite history:
```bash
git rebase -i HEAD~3   # edit last 3 commits
```
Options: `pick`, `squash`, `reword`, `edit`, `drop`

### ✅ Task 5 — Merging
- [ ] Switch to `main` and merge `feature/about-page`
- [ ] Check that `about.txt` now exists on `main`
- [ ] Try creating a conflict: edit the same line in two branches, then merge

---

## 8. Remote Repositories

### Add a Remote
```bash
git remote add origin https://github.com/user/repo.git
git remote -v                    # view remotes
git remote rename origin upstream
git remote remove origin
```

### Push
```bash
git push origin main             # push main branch to origin
git push -u origin main          # set upstream (tracking), then just use git push
git push --force-with-lease      # safer force push
git push origin --delete feature/old   # delete remote branch
```

### Fetch vs Pull
```bash
git fetch origin          # download changes, don't integrate
git fetch --all           # fetch from all remotes

git pull origin main      # fetch + merge
git pull --rebase         # fetch + rebase (cleaner history)
```

> 💡 Prefer `git fetch` + manual merge/rebase over `git pull` for more control.

### Track Remote Branches
```bash
git branch -u origin/main main     # set tracking manually
git branch -vv                     # see tracking info
```

### ✅ Task 6 — Remotes
- [ ] Create a free repo on GitHub
- [ ] Add it as a remote: `git remote add origin <url>`
- [ ] Push your commits: `git push -u origin main`
- [ ] View your repo on GitHub

---

## 9. Undoing Things

### Amend Last Commit
```bash
git commit --amend -m "Corrected commit message"
git commit --amend --no-edit       # amend without changing message (e.g., add missed file)
```

### Restore / Discard Changes
```bash
git restore hello.txt             # discard working directory changes
git restore --staged hello.txt    # unstage (keep changes in working dir)
git restore --source HEAD~2 hello.txt  # restore file from 2 commits ago
```

### Reset
```bash
git reset --soft HEAD~1    # undo commit, keep changes STAGED
git reset --mixed HEAD~1   # undo commit, keep changes in WORKING DIR (default)
git reset --hard HEAD~1    # undo commit, DISCARD all changes ⚠️
```

### Revert (Safe Undo for Shared Branches)
```bash
git revert HEAD            # create a new commit that undoes the last commit
git revert abc1234         # revert a specific commit by hash
```

> ✅ Use `revert` on shared/public branches. Use `reset` only on local/private branches.

### Clean Untracked Files
```bash
git clean -n    # dry run (show what would be deleted)
git clean -f    # delete untracked files
git clean -fd   # delete untracked files AND directories
```

### ✅ Task 7 — Undoing
- [ ] Make a change to a file, then use `git restore` to discard it
- [ ] Make a commit, then use `git reset --soft HEAD~1` to undo it
- [ ] Use `git revert` to undo a commit safely

---

## 10. Stashing

Stash saves uncommitted work temporarily so you can switch context.

```bash
git stash                         # stash current changes
git stash push -m "WIP: login UI" # stash with a description
git stash list                    # view all stashes
git stash pop                     # apply most recent stash + delete it
git stash apply stash@{2}         # apply a specific stash (keep it)
git stash drop stash@{0}          # delete a specific stash
git stash clear                   # delete all stashes
git stash branch feature/new      # create branch from stash
```

### Example Use Case
```bash
# You're mid-feature when an urgent bug comes in
git stash push -m "WIP: checkout form"

# Fix the bug on main
git switch main
# ... fix bug, commit ...

# Return to your feature
git switch feature/checkout
git stash pop
```

### ✅ Task 8 — Stashing
- [ ] Make some uncommitted changes
- [ ] Run `git stash`
- [ ] Verify your changes are gone with `git status`
- [ ] Run `git stash pop` to bring them back

---

## 11. Tags

Tags mark specific points in history — usually releases.

```bash
git tag                          # list all tags
git tag v1.0.0                   # lightweight tag
git tag -a v1.0.0 -m "Release 1.0.0"  # annotated tag (recommended)
git tag -a v0.9.0 abc1234        # tag a past commit
git show v1.0.0                  # view tag details
git push origin v1.0.0           # push a specific tag
git push origin --tags           # push all tags
git tag -d v1.0.0                # delete local tag
git push origin --delete v1.0.0  # delete remote tag
```

### Semantic Versioning Convention
```
v MAJOR . MINOR . PATCH
   1   .   2   .   3

MAJOR — breaking changes
MINOR — new features (backward compatible)
PATCH — bug fixes
```

### ✅ Task 9 — Tagging
- [ ] Tag your current commit as `v0.1.0` with an annotation
- [ ] Run `git tag` to verify
- [ ] Run `git show v0.1.0`

---

## 12. Git Log & History

### Basic Log
```bash
git log                         # full log
git log --oneline               # compact, one line per commit
git log --oneline --graph --all # visual branch tree
git log -5                      # last 5 commits
git log --author="Jane"         # filter by author
git log --since="2 weeks ago"   # filter by date
git log --grep="fix"            # search commit messages
git log -- path/to/file         # commits that touched a file
```

### Inspect a Commit
```bash
git show abc1234          # show commit details + diff
git show HEAD             # show latest commit
git show HEAD~2           # show 2 commits ago
```

### Blame
```bash
git blame hello.txt       # show who last modified each line
git blame -L 10,20 hello.txt  # only lines 10-20
```

### Search Content History
```bash
git log -S "functionName"   # find when a string was added/removed
git log -G "regex"          # search with regex
```

### Reflog (Your Safety Net)
```bash
git reflog          # shows ALL HEAD movements, even after reset
# Use this to recover "lost" commits!
git reset --hard HEAD@{3}   # go back to a previous reflog position
```

### ✅ Task 10 — History
- [ ] Run `git log --oneline --graph --all`
- [ ] Use `git blame` on one of your files
- [ ] Use `git log --grep` to search for a commit message keyword

---

## 13. Advanced Topics

### Cherry-Pick
Apply a specific commit from another branch onto the current branch.
```bash
git cherry-pick abc1234
git cherry-pick abc1234 def5678   # multiple commits
git cherry-pick --no-commit abc1234  # apply without committing
```

### Bisect (Find a Bug with Binary Search)
```bash
git bisect start
git bisect bad                  # current commit is bad
git bisect good v1.0.0          # last known good commit
# Git checks out a middle commit — test it, then:
git bisect good                 # or git bisect bad
# Repeat until Git identifies the offending commit
git bisect reset                # finish bisecting
```

### Submodules
Include another Git repo inside your repo.
```bash
git submodule add https://github.com/user/lib.git libs/lib
git submodule update --init --recursive   # initialize after cloning
```

### .gitignore
Tell Git to ignore files:
```
# .gitignore
node_modules/
.env
*.log
dist/
.DS_Store
*.pyc
```

```bash
git check-ignore -v filename    # debug why a file is ignored
git rm --cached filename        # stop tracking a file (already committed)
```

### Git Hooks
Scripts that run automatically on Git events (stored in `.git/hooks/`):

| Hook | Trigger |
|------|---------|
| `pre-commit` | Before a commit is created |
| `commit-msg` | Validate commit message |
| `pre-push` | Before push to remote |
| `post-merge` | After a merge |

```bash
# Example: .git/hooks/pre-commit
#!/bin/sh
npm test   # run tests before every commit
```

### Aliases
```bash
git config --global alias.st status
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.lg "log --oneline --graph --all"
```

---

## 14. Workflows

### Feature Branch Workflow
```
main ──────────────────────────────► (production)
         \               /
          feature/login ►
```
1. Branch off `main`
2. Work on your feature
3. Open a Pull Request
4. Code review
5. Merge into `main`

### Gitflow Workflow
```
main        ──────────────────────────────► (releases)
develop     ───────────────────────────────► (integration)
feature/*   ──►  merged into develop
release/*   ──►  branched from develop, merged into main + develop
hotfix/*    ──►  branched from main, merged into main + develop
```

### Trunk-Based Development
- Everyone commits directly to `main` (or short-lived branches)
- Use feature flags to hide incomplete features
- Requires strong CI/CD

### ✅ Final Project
Build a mini project using the Feature Branch Workflow:
- [ ] Create a repo with `README.md` on `main`
- [ ] Create `feature/add-homepage`
- [ ] Add `index.html` and commit
- [ ] Create `feature/add-styles`
- [ ] Add `style.css` and commit
- [ ] Merge both features into `main`
- [ ] Tag it as `v1.0.0`
- [ ] Push everything to GitHub

---

## 15. Cheatsheet

```bash
# ── SETUP ──────────────────────────────────────────
git config --global user.name "Name"
git config --global user.email "email"

# ── INIT ───────────────────────────────────────────
git init                    # new local repo
git clone <url>             # clone remote repo

# ── BASIC WORKFLOW ─────────────────────────────────
git status                  # check state
git add <file>              # stage file
git add .                   # stage all
git commit -m "message"     # commit
git commit --amend          # fix last commit

# ── BRANCHING ──────────────────────────────────────
git branch                  # list branches
git switch -c <branch>      # create + switch
git switch <branch>         # switch branch
git branch -d <branch>      # delete branch
git merge <branch>          # merge into current
git rebase <branch>         # rebase onto branch

# ── REMOTE ─────────────────────────────────────────
git remote add origin <url>
git push -u origin main
git fetch origin
git pull origin main

# ── UNDOING ────────────────────────────────────────
git restore <file>          # discard working changes
git restore --staged <file> # unstage
git reset --soft HEAD~1     # undo commit, keep staged
git reset --hard HEAD~1     # undo commit, lose changes ⚠️
git revert HEAD             # safe undo (new commit)

# ── STASH ──────────────────────────────────────────
git stash                   # stash changes
git stash pop               # restore stash
git stash list              # view stashes

# ── LOG ────────────────────────────────────────────
git log --oneline --graph --all
git blame <file>
git show <commit>
git reflog

# ── TAGS ───────────────────────────────────────────
git tag -a v1.0.0 -m "Release"
git push origin --tags
```

---

## 📖 Resources

| Resource | Link |
|----------|------|
| Official Git Docs | https://git-scm.com/doc |
| Pro Git Book (free) | https://git-scm.com/book |
| Learn Git Branching (interactive) | https://learngitbranching.js.org |
| GitHub Docs | https://docs.github.com |
| Oh Shit, Git!? (quick fixes) | https://ohshitgit.com |

---

*Happy committing! 🚀*
