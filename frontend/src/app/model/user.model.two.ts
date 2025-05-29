// Définir un type pour les rôles
type Role = "MANAGER" | "EMPLOYEE" | "SECRETARY";

interface RoleDetails {
  name: string;        // Nom du rôle
}

export const ROLES = {
  MANAGER: {
    name: "MANAGER",
  },
  EMPLOYEE: {
    name: "EMPLOYEE",
  },
  SECRETARY: {
    name: "SECRETARY",
  },
} as const;
