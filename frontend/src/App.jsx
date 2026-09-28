import { useEffect, useMemo, useState } from 'react'
import { api, clearCredentials, hasCredentials, saveCredentials } from './api'

const STATUSES = ['TODO', 'IN_PROGRESS', 'DONE']
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH']

function App() {
  const [user, setUser] = useState(null)
  const [projects, setProjects] = useState([])
  const [assignments, setAssignments] = useState([])
  const [search, setSearch] = useState('')
  const [status, setStatus] = useState('')
  const [selectedProject, setSelectedProject] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const loadSession = async () => {
    if (!hasCredentials()) return
    try {
      const me = await api('/auth/me')
      setUser(me)
    } catch {
      clearCredentials()
    }
  }

  useEffect(() => { loadSession() }, [])

  useEffect(() => {
    if (!user) return
    Promise.all([api('/projects'), loadAssignments()])
      .then(([projectData]) => setProjects(projectData))
      .catch(err => setError(err.message))
  }, [user])

  const loadAssignments = async () => {
    const params = new URLSearchParams()
    if (search) params.set('search', search)
    if (status) params.set('status', status)
    if (selectedProject) params.set('projectId', selectedProject)
    const data = await api(`/assignments?${params.toString()}`)
    setAssignments(data)
    return data
  }

  const handleLogin = async (username, password) => {
    setLoading(true)
    setError('')
    saveCredentials(username, password)
    try {
      const me = await api('/auth/me')
      setUser(me)
    } catch (err) {
      clearCredentials()
      setError(err.status === 401 ? 'Invalid username or password.' : err.message)
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    clearCredentials()
    setUser(null)
    setProjects([])
    setAssignments([])
  }

  if (!user) return <Login onLogin={handleLogin} loading={loading} error={error} />

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <div className="brand-mark">TF</div>
          <h1>TaskFlow</h1>
          <p className="muted">Collaborative workspace</p>
        </div>
        <nav>
          <a className="nav-active" href="#dashboard">Dashboard</a>
          <a href="#projects">Projects</a>
          <a href="#assignments">Assignments</a>
        </nav>
        <div className="user-card">
          <strong>{user.displayName}</strong>
          <span>{user.role}</span>
          <button className="text-button" onClick={logout}>Sign out</button>
        </div>
      </aside>

      <main className="content" id="dashboard">
        <header className="page-header">
          <div>
            <p className="eyebrow">WORKSPACE OVERVIEW</p>
            <h2>Keep the team moving.</h2>
            <p className="muted">Search assignments, update progress, and keep project discussion in one place.</p>
          </div>
          <div className="stats">
            <Stat label="Projects" value={projects.length} />
            <Stat label="Open tasks" value={assignments.filter(a => a.status !== 'DONE').length} />
            <Stat label="Completed" value={assignments.filter(a => a.status === 'DONE').length} />
          </div>
        </header>

        {error && <div className="error-banner">{error}</div>}

        {user.role === 'ADMIN' && (
          <AdminPanel projects={projects} onChanged={async () => {
            setProjects(await api('/projects'))
            await loadAssignments()
          }} />
        )}

        <section className="panel" id="assignments">
          <div className="panel-heading">
            <div>
              <p className="eyebrow">ASSIGNMENTS</p>
              <h3>Team work queue</h3>
            </div>
            <button className="button secondary" onClick={() => loadAssignments().catch(err => setError(err.message))}>Refresh</button>
          </div>

          <div className="filters">
            <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search title or description" />
            <select value={status} onChange={e => setStatus(e.target.value)}>
              <option value="">All statuses</option>
              {STATUSES.map(s => <option key={s} value={s}>{pretty(s)}</option>)}
            </select>
            <select value={selectedProject} onChange={e => setSelectedProject(e.target.value)}>
              <option value="">All projects</option>
              {projects.map(p => <option key={p.id} value={p.id}>{p.title}</option>)}
            </select>
            <button className="button" onClick={() => loadAssignments().catch(err => setError(err.message))}>Apply filters</button>
          </div>

          <div className="task-grid">
            {assignments.map(task => (
              <TaskCard key={task.id} task={task} onChanged={() => loadAssignments().catch(err => setError(err.message))} />
            ))}
            {assignments.length === 0 && <p className="empty">No assignments match these filters.</p>}
          </div>
        </section>

        <section className="panel" id="projects">
          <div className="panel-heading">
            <div>
              <p className="eyebrow">PROJECTS</p>
              <h3>Active project spaces</h3>
            </div>
          </div>
          <div className="project-grid">
            {projects.map(project => <ProjectCard key={project.id} project={project} />)}
          </div>
        </section>
      </main>
    </div>
  )
}

function Login({ onLogin, loading, error }) {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin123')

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={e => { e.preventDefault(); onLogin(username, password) }}>
        <div className="brand-mark large">TF</div>
        <p className="eyebrow">COLLABORATIVE TASK PLATFORM</p>
        <h1>Welcome back</h1>
        <p className="muted">Use the seeded demo account or sign in with a local account.</p>
        {error && <div className="error-banner">{error}</div>}
        <label>Username<input value={username} onChange={e => setUsername(e.target.value)} /></label>
        <label>Password<input type="password" value={password} onChange={e => setPassword(e.target.value)} /></label>
        <button className="button full" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button>
        <p className="login-hint">Admin: admin / admin123 · Member: member / member123</p>
      </form>
    </div>
  )
}

function AdminPanel({ projects, onChanged }) {
  const [projectTitle, setProjectTitle] = useState('')
  const [projectDescription, setProjectDescription] = useState('')
  const [task, setTask] = useState({ title: '', description: '', projectId: '', assigneeUsername: 'member', priority: 'MEDIUM' })
  const [message, setMessage] = useState('')

  const createProject = async e => {
    e.preventDefault()
    await api('/projects', { method: 'POST', body: JSON.stringify({ title: projectTitle, description: projectDescription }) })
    setProjectTitle(''); setProjectDescription(''); setMessage('Project created.')
    await onChanged()
  }

  const createAssignment = async e => {
    e.preventDefault()
    await api('/assignments', { method: 'POST', body: JSON.stringify({ ...task, projectId: Number(task.projectId) }) })
    setTask({ title: '', description: '', projectId: task.projectId, assigneeUsername: 'member', priority: 'MEDIUM' })
    setMessage('Assignment created.')
    await onChanged()
  }

  return (
    <section className="admin-grid">
      <form className="panel compact" onSubmit={createProject}>
        <p className="eyebrow">ADMIN</p><h3>Create project</h3>
        <input required placeholder="Project title" value={projectTitle} onChange={e => setProjectTitle(e.target.value)} />
        <textarea required placeholder="Project description" value={projectDescription} onChange={e => setProjectDescription(e.target.value)} />
        <button className="button">Create project</button>
      </form>

      <form className="panel compact" onSubmit={createAssignment}>
        <p className="eyebrow">ADMIN</p><h3>Create assignment</h3>
        <input required placeholder="Assignment title" value={task.title} onChange={e => setTask({ ...task, title: e.target.value })} />
        <textarea required placeholder="Assignment description" value={task.description} onChange={e => setTask({ ...task, description: e.target.value })} />
        <div className="inline-fields">
          <select required value={task.projectId} onChange={e => setTask({ ...task, projectId: e.target.value })}>
            <option value="">Choose project</option>
            {projects.map(p => <option key={p.id} value={p.id}>{p.title}</option>)}
          </select>
          <select value={task.priority} onChange={e => setTask({ ...task, priority: e.target.value })}>
            {PRIORITIES.map(p => <option key={p}>{pretty(p)}</option>)}
          </select>
        </div>
        <input placeholder="Assignee username" value={task.assigneeUsername} onChange={e => setTask({ ...task, assigneeUsername: e.target.value })} />
        <button className="button">Create assignment</button>
        {message && <small className="success">{message}</small>}
      </form>
    </section>
  )
}

function TaskCard({ task, onChanged }) {
  const [expanded, setExpanded] = useState(false)
  const [comments, setComments] = useState([])
  const [comment, setComment] = useState('')

  const loadComments = async () => setComments(await api(`/assignments/${task.id}/comments`))
  const toggle = async () => {
    const next = !expanded
    setExpanded(next)
    if (next) await loadComments()
  }
  const updateStatus = async status => {
    await api(`/assignments/${task.id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) })
    onChanged()
  }
  const addComment = async e => {
    e.preventDefault()
    if (!comment.trim()) return
    await api(`/assignments/${task.id}/comments`, { method: 'POST', body: JSON.stringify({ body: comment }) })
    setComment('')
    await loadComments()
  }

  return (
    <article className="task-card">
      <div className="task-topline">
        <span className={`priority ${task.priority.toLowerCase()}`}>{task.priority}</span>
        <span className="project-chip">{task.projectTitle}</span>
      </div>
      <h4>{task.title}</h4>
      <p>{task.description}</p>
      <div className="task-meta">
        <span>Assignee: {task.assignee?.displayName || 'Unassigned'}</span>
        <span>{task.dueDate ? `Due ${task.dueDate}` : 'No due date'}</span>
      </div>
      <div className="task-actions">
        <select value={task.status} onChange={e => updateStatus(e.target.value)}>
          {STATUSES.map(s => <option key={s} value={s}>{pretty(s)}</option>)}
        </select>
        <button className="text-button" onClick={toggle}>{expanded ? 'Hide discussion' : 'Discussion'}</button>
      </div>
      {expanded && (
        <div className="comments">
          {comments.map(c => <div className="comment" key={c.id}><strong>{c.author.displayName}</strong><span>{c.body}</span></div>)}
          <form onSubmit={addComment} className="comment-form">
            <input placeholder="Add a comment" value={comment} onChange={e => setComment(e.target.value)} />
            <button className="button small">Post</button>
          </form>
        </div>
      )}
    </article>
  )
}

function ProjectCard({ project }) {
  const [activity, setActivity] = useState(null)
  const load = async () => setActivity(await api(`/projects/${project.id}/activity`))
  return (
    <article className="project-card">
      <div className="project-icon">{project.title.slice(0, 2).toUpperCase()}</div>
      <div>
        <h4>{project.title}</h4>
        <p>{project.description}</p>
        <small>Owner: {project.owner.displayName}</small>
        <button className="text-button block" onClick={load}>View activity</button>
        {activity && <ul className="activity-list">{activity.slice(0, 5).map(item => <li key={item.id}>{item.details}</li>)}</ul>}
      </div>
    </article>
  )
}

function Stat({ label, value }) {
  return <div className="stat"><strong>{value}</strong><span>{label}</span></div>
}

function pretty(value) {
  return value.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, c => c.toUpperCase())
}

export default App
