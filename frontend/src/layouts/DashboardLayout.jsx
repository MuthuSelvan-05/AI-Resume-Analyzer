import { NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  BarChart3,
  BriefcaseBusiness,
  FileText,
  GraduationCap,
  LayoutDashboard,
  LogOut,
  MessageSquareText,
  Settings,
  Sparkles,
} from "lucide-react";

import useAuth from "../hooks/useAuth";

const navigation = [
  {
    name: "Dashboard",
    path: "/dashboard",
    icon: LayoutDashboard,
  },
  {
    name: "Resumes",
    path: "/resumes",
    icon: FileText,
  },
  {
    name: "Jobs",
    path: "/jobs",
    icon: BriefcaseBusiness,
  },
  {
    name: "Analysis",
    path: "/analysis",
    icon: BarChart3,
  },
  {
    name: "Skill Roadmap",
    path: "/roadmap",
    icon: GraduationCap,
  },
  {
    name: "Interview Prep",
    path: "/interview",
    icon: MessageSquareText,
  },
];

function DashboardLayout() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="flex min-h-screen">
        <aside className="hidden w-64 border-r border-slate-800 bg-slate-900/80 lg:flex lg:flex-col">
          <div className="flex h-16 items-center gap-3 border-b border-slate-800 px-6">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-blue-600">
              <Sparkles size={19} />
            </div>

            <div>
              <h1 className="text-sm font-semibold">ResumeAI</h1>
              <p className="text-xs text-slate-500">Career Intelligence</p>
            </div>
          </div>

          <nav className="flex-1 space-y-1 p-4">
            {navigation.map((item) => {
              const Icon = item.icon;

              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  className={({ isActive }) =>
                    `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm transition ${
                      isActive
                        ? "bg-blue-600/15 text-blue-400"
                        : "text-slate-400 hover:bg-slate-800 hover:text-slate-100"
                    }`
                  }
                >
                  <Icon size={18} />
                  {item.name}
                </NavLink>
              );
            })}
          </nav>

          <div className="border-t border-slate-800 p-4">
            <button className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm text-slate-400 transition hover:bg-slate-800 hover:text-slate-100">
              <Settings size={18} />
              Settings
            </button>

            <button
              onClick={handleLogout}
              className="mt-1 flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm text-slate-400 transition hover:bg-red-500/10 hover:text-red-400"
            >
              <LogOut size={18} />
              Sign out
            </button>
          </div>
        </aside>

        <div className="flex min-w-0 flex-1 flex-col">
          <header className="flex h-16 items-center justify-between border-b border-slate-800 bg-slate-950/90 px-6">
            <div>
              <p className="text-sm font-medium">AI Resume Analyzer</p>
              <p className="text-xs text-slate-500">
                Analyze. Improve. Prepare.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <div className="hidden text-right sm:block">
                <p className="text-sm font-medium">
                  {user?.name || "User"}
                </p>
                <p className="text-xs text-slate-500">
                  {user?.email || ""}
                </p>
              </div>

              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-slate-800 text-sm font-semibold">
                {(user?.name || "U").charAt(0).toUpperCase()}
              </div>
            </div>
          </header>

          <main className="flex-1 overflow-auto p-6 lg:p-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}

export default DashboardLayout;