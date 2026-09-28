# GitHub setup

Use these commands from inside the project folder after creating an empty GitHub repository named `collaborative-task-platform`.

```bash
git init
git branch -M main
git add .
git commit -m "feat: rebuild collaborative task platform"
git remote add origin https://github.com/YOUR_USERNAME/collaborative-task-platform.git
git push -u origin main
```

For a more realistic reconstruction history, make commits as you verify each layer locally instead of committing the entire repository at once. The README includes suggested commit messages tied to actual pieces of the project.

Before pushing, run:

```bash
docker compose up -d
cd backend
mvn test
```

Then in a second terminal:

```bash
cd frontend
npm install
npm run build
```

Do not commit `node_modules`, backend `target`, `.env` files, or real credentials. The supplied `.gitignore` already excludes them.
