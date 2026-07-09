CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE job_roles
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    slug         VARCHAR(120) NOT NULL,
    label        VARCHAR(160) NOT NULL,
    description  VARCHAR(500),
    family       VARCHAR(80)  NOT NULL,

    tags_json    JSONB        NOT NULL DEFAULT '[]'::jsonb,
    aliases_json JSONB        NOT NULL DEFAULT '[]'::jsonb,

    sort_order   INT          NOT NULL DEFAULT 0,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_job_roles_slug UNIQUE (slug),

    CONSTRAINT chk_job_roles_family CHECK (
        family IN (
                   'SOFTWARE_ENGINEERING',
                   'PRODUCT_DESIGN',
                   'DATA',
                   'DEVOPS_CLOUD',
                   'PROJECT_MANAGEMENT',
                   'PRODUCT_MANAGEMENT',
                   'QUALITY_ASSURANCE',
                   'BUSINESS_ANALYSIS',
                   'OTHER'
            )
        )
);

CREATE INDEX idx_job_roles_active_sort
    ON job_roles (active, sort_order);

CREATE INDEX idx_job_roles_family
    ON job_roles (family);

CREATE INDEX idx_job_roles_label
    ON job_roles (label);

INSERT INTO job_roles (
    slug,
    label,
    description,
    family,
    tags_json,
    aliases_json,
    sort_order
)
VALUES
    (
        'frontend-developer',
        'Développeur frontend',
        'Interfaces web modernes, Angular, React, TypeScript, HTML, CSS.',
        'SOFTWARE_ENGINEERING',
        '["frontend", "angular", "react", "typescript", "html", "css", "accessibility"]'::jsonb,
        '["développeur front", "frontend engineer", "intégrateur frontend"]'::jsonb,
        10
    ),
    (
        'backend-developer',
        'Développeur backend',
        'API, logique serveur, bases de données, sécurité et performance backend.',
        'SOFTWARE_ENGINEERING',
        '["backend", "api", "rest", "sql", "security", "performance"]'::jsonb,
        '["développeur back", "backend engineer"]'::jsonb,
        20
    ),
    (
        'fullstack-developer',
        'Développeur fullstack',
        'Développement frontend et backend, API, base de données et intégration.',
        'SOFTWARE_ENGINEERING',
        '["fullstack", "frontend", "backend", "api", "sql", "typescript"]'::jsonb,
        '["développeur full stack", "fullstack engineer"]'::jsonb,
        30
    ),
    (
        'java-angular-developer',
        'Développeur Java Angular',
        'Spring Boot, Angular, API REST, PostgreSQL, sécurité et architecture fullstack.',
        'SOFTWARE_ENGINEERING',
        '["java", "spring_boot", "angular", "rest", "postgresql", "security", "jpa"]'::jsonb,
        '["développeur spring angular", "fullstack java angular", "java angular developer"]'::jsonb,
        40
    ),
    (
        'angular-developer',
        'Développeur Angular',
        'Applications Angular modernes, composants, signals, routing, formulaires et performance.',
        'SOFTWARE_ENGINEERING',
        '["angular", "typescript", "signals", "rxjs", "frontend", "performance"]'::jsonb,
        '["angular developer", "frontend angular", "développeur frontend angular"]'::jsonb,
        50
    ),
    (
        'spring-boot-developer',
        'Développeur Spring Boot',
        'APIs Java, Spring Boot, JPA, sécurité, tests et architecture backend.',
        'SOFTWARE_ENGINEERING',
        '["java", "spring_boot", "jpa", "hibernate", "security", "rest", "testing"]'::jsonb,
        '["développeur java spring", "spring boot developer", "backend java"]'::jsonb,
        60
    ),
    (
        'ux-ui-designer',
        'UX/UI Designer',
        'Recherche utilisateur, wireframes, interfaces, design system et accessibilité.',
        'PRODUCT_DESIGN',
        '["ux", "ui", "figma", "wireframes", "design_system", "accessibility"]'::jsonb,
        '["designer ux ui", "ui designer", "ux designer"]'::jsonb,
        70
    ),
    (
        'product-designer',
        'Product Designer',
        'Conception produit, expérience utilisateur, stratégie UX et design d’interface.',
        'PRODUCT_DESIGN',
        '["product_design", "ux", "ui", "discovery", "design_system"]'::jsonb,
        '["designer produit", "product design"]'::jsonb,
        80
    ),
    (
        'data-analyst',
        'Data Analyst',
        'Analyse de données, SQL, dashboards, KPIs et visualisation.',
        'DATA',
        '["data", "sql", "dashboard", "kpi", "analytics", "visualization"]'::jsonb,
        '["analyste data", "business data analyst"]'::jsonb,
        90
    ),
    (
        'devops-engineer',
        'DevOps Engineer',
        'CI/CD, Docker, cloud, monitoring, déploiement et automatisation.',
        'DEVOPS_CLOUD',
        '["devops", "docker", "ci_cd", "cloud", "monitoring", "deployment"]'::jsonb,
        '["ingénieur devops", "devops junior"]'::jsonb,
        100
    ),
    (
        'azure-cloud-engineer',
        'Cloud Engineer Azure',
        'Azure, ressources cloud, réseau, sécurité, déploiement et supervision.',
        'DEVOPS_CLOUD',
        '["azure", "cloud", "networking", "security", "deployment", "monitoring"]'::jsonb,
        '["azure engineer", "cloud azure", "azure devops"]'::jsonb,
        110
    ),
    (
        'project-manager-it',
        'Chef de projet IT',
        'Pilotage projet, coordination, planning, communication et suivi de livraison.',
        'PROJECT_MANAGEMENT',
        '["project_management", "planning", "agile", "communication", "delivery"]'::jsonb,
        '["chef de projet digital", "it project manager"]'::jsonb,
        120
    ),
    (
        'product-owner',
        'Product Owner',
        'Backlog, priorisation, besoins métier, user stories et coordination produit.',
        'PRODUCT_MANAGEMENT',
        '["product_owner", "backlog", "agile", "user_stories", "prioritization"]'::jsonb,
        '["po", "product manager junior"]'::jsonb,
        130
    ),
    (
        'qa-tester',
        'QA Tester',
        'Tests fonctionnels, automatisation, qualité logicielle et scénarios de validation.',
        'QUALITY_ASSURANCE',
        '["qa", "testing", "automation", "quality", "test_cases"]'::jsonb,
        '["testeur qa", "software tester", "qa analyst"]'::jsonb,
        140
    ),
    (
        'business-analyst',
        'Business Analyst',
        'Analyse des besoins, processus métier, documentation et interface métier-technique.',
        'BUSINESS_ANALYSIS',
        '["business_analysis", "requirements", "process", "documentation", "stakeholders"]'::jsonb,
        '["analyste fonctionnel", "business analyst it"]'::jsonb,
        150
    );

ALTER TABLE user_profiles
    ADD COLUMN target_role_id UUID,
    ADD COLUMN target_role_label VARCHAR(180),
    ADD COLUMN target_role_source VARCHAR(40) NOT NULL DEFAULT 'CUSTOM';

ALTER TABLE user_profiles
    ADD CONSTRAINT fk_user_profiles_target_role
        FOREIGN KEY (target_role_id)
            REFERENCES job_roles (id)
            ON DELETE SET NULL;

ALTER TABLE user_profiles
    ADD CONSTRAINT chk_user_profiles_target_role_source CHECK (
        target_role_source IN ('CATALOG', 'CUSTOM', 'OFFER_ANALYSIS')
        );

CREATE INDEX idx_user_profiles_target_role_id
    ON user_profiles (target_role_id);

CREATE INDEX idx_user_profiles_target_role_source
    ON user_profiles (target_role_source);

CREATE INDEX idx_user_profiles_target_role_label
    ON user_profiles (target_role_label);

UPDATE user_profiles
SET target_role_label = target_role
WHERE target_role IS NOT NULL
  AND target_role_label IS NULL;

UPDATE user_profiles up
SET
    target_role_id = jr.id,
    target_role_source = 'CATALOG'
    FROM job_roles jr
WHERE up.target_role_label IS NOT NULL
  AND lower(trim(up.target_role_label)) = lower(trim(jr.label));

ALTER TABLE user_profiles
DROP COLUMN target_role;