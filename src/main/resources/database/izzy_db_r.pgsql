--
-- PostgreSQL database dump
--

-- Dumped from database version 12.20 (Ubuntu 12.20-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.20 (Ubuntu 12.20-0ubuntu0.20.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS users_zones_fk;
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS users_users_fk;
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS user_head_fk;
ALTER TABLE IF EXISTS ONLY public.scooters DROP CONSTRAINT IF EXISTS scooters_zones_fk;
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS orders_updated_by_fkey;
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS orders_created_by_fkey;
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS orders_assigned_to_fkey;
ALTER TABLE IF EXISTS ONLY public.notifications DROP CONSTRAINT IF EXISTS notifications_users_fk;
ALTER TABLE IF EXISTS ONLY public.notifications DROP CONSTRAINT IF EXISTS notifications_tasks_fk;
ALTER TABLE IF EXISTS ONLY public.refreshtoken DROP CONSTRAINT IF EXISTS "FK_refreshtoken_user_id";
ALTER TABLE IF EXISTS ONLY public.user_roles DROP CONSTRAINT IF EXISTS "FK_UserRole_User_Id";
ALTER TABLE IF EXISTS ONLY public.user_roles DROP CONSTRAINT IF EXISTS "FK_UserRole_Role_Id";
ALTER TABLE IF EXISTS ONLY public.tasks DROP CONSTRAINT IF EXISTS "FK_Task_Scooter_Id";
ALTER TABLE IF EXISTS ONLY public.tasks DROP CONSTRAINT IF EXISTS "FK_Task_Order_Id";
ALTER TABLE IF EXISTS ONLY public.zones DROP CONSTRAINT IF EXISTS zones_pkey;
ALTER TABLE IF EXISTS ONLY public.zones DROP CONSTRAINT IF EXISTS "zones_name unique";
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS users_pkey;
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS users_phone_number_key;
ALTER TABLE IF EXISTS ONLY public.scooters DROP CONSTRAINT IF EXISTS scooters_pkey;
ALTER TABLE IF EXISTS ONLY public.scooters DROP CONSTRAINT IF EXISTS scooters_identifier_unique;
ALTER TABLE IF EXISTS ONLY public.roles DROP CONSTRAINT IF EXISTS roles_name_unique;
ALTER TABLE IF EXISTS ONLY public.refreshtoken DROP CONSTRAINT IF EXISTS refresh_unique_user_id;
ALTER TABLE IF EXISTS ONLY public.refreshtoken DROP CONSTRAINT IF EXISTS refresh_unique_token;
ALTER TABLE IF EXISTS ONLY public.refreshtoken DROP CONSTRAINT IF EXISTS refresh_pk;
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS orders_pkey;
ALTER TABLE IF EXISTS ONLY public.orders DROP CONSTRAINT IF EXISTS orders_name_unique;
ALTER TABLE IF EXISTS ONLY public.notifications DROP CONSTRAINT IF EXISTS notifications_unique;
ALTER TABLE IF EXISTS ONLY public.notifications DROP CONSTRAINT IF EXISTS notifications_pk;
ALTER TABLE IF EXISTS ONLY public.roles DROP CONSTRAINT IF EXISTS "PK_roles";
ALTER TABLE IF EXISTS ONLY public.user_roles DROP CONSTRAINT IF EXISTS "PK_UserRole";
ALTER TABLE IF EXISTS ONLY public.tasks DROP CONSTRAINT IF EXISTS "PK_Task";
ALTER TABLE IF EXISTS public.zones ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.scooters ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.roles ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.orders ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.notifications ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.zones_id_seq;
DROP TABLE IF EXISTS public.zones;
DROP SEQUENCE IF EXISTS public.users_id_seq;
DROP TABLE IF EXISTS public.users;
DROP TABLE IF EXISTS public.user_roles;
DROP TABLE IF EXISTS public.tasks;
DROP SEQUENCE IF EXISTS public.scooters_id_seq;
DROP TABLE IF EXISTS public.scooters;
DROP SEQUENCE IF EXISTS public.roles_id_seq;
DROP TABLE IF EXISTS public.roles;
DROP TABLE IF EXISTS public.refreshtoken;
DROP SEQUENCE IF EXISTS public.refreshtoken_id_seq;
DROP SEQUENCE IF EXISTS public.orders_id_seq;
DROP TABLE IF EXISTS public.orders;
DROP SEQUENCE IF EXISTS public.notifications_id_seq;
DROP TABLE IF EXISTS public.notifications;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: notifications; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.notifications (
    user_id bigint NOT NULL,
    id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0) NOT NULL,
    order_id bigint NOT NULL,
    scooter_id bigint NOT NULL,
    user_action character varying,
    CONSTRAINT notifications_action_check CHECK (((user_action)::text = ANY (ARRAY['approved'::text, 'rejected'::text])))
);


ALTER TABLE public.notifications OWNER TO root;

--
-- Name: COLUMN notifications.user_id; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.notifications.user_id IS 'responsible/reactable user';


--
-- Name: COLUMN notifications.created_at; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.notifications.created_at IS 'notification creation time';


--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO root;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: orders; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.orders (
    id bigint NOT NULL,
    action character varying(255),
    name character varying(255),
    description character varying(255),
    created_by bigint,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_by bigint,
    updated_at timestamp with time zone,
    assigned_to bigint,
    status character varying(255),
    taken_at timestamp with time zone,
    done_at timestamp with time zone,
    CONSTRAINT orders_action_check CHECK (((action)::text = ANY (ARRAY[('Move'::character varying)::text, ('Charge'::character varying)::text, ('Repair'::character varying)::text]))),
    CONSTRAINT users_status_check CHECK (((status)::text = ANY (ARRAY['Created'::text, 'Assigned'::text, 'In_Progress'::text, 'Completed'::text, 'Canceled'::text])))
);


ALTER TABLE public.orders OWNER TO root;

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO root;

--
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- Name: refreshtoken_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.refreshtoken_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.refreshtoken_id_seq OWNER TO root;

--
-- Name: refreshtoken; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.refreshtoken (
    id bigint DEFAULT nextval('public.refreshtoken_id_seq'::regclass) NOT NULL,
    expiry_date timestamp with time zone,
    current_token character varying(255),
    user_id bigint NOT NULL
);


ALTER TABLE public.refreshtoken OWNER TO root;

--
-- Name: TABLE refreshtoken; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON TABLE public.refreshtoken IS 'information on the refresh token';


--
-- Name: COLUMN refreshtoken.current_token; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.refreshtoken.current_token IS 'current refresh token';


--
-- Name: COLUMN refreshtoken.user_id; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.refreshtoken.user_id IS 'owner of the token';


--
-- Name: roles; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY (ARRAY[('Admin'::character varying)::text, ('Manager'::character varying)::text, ('Supervisor'::character varying)::text, ('Charger'::character varying)::text, ('Scout'::character varying)::text])))
);


ALTER TABLE public.roles OWNER TO root;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_id_seq OWNER TO root;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- Name: scooters; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.scooters (
    id bigint NOT NULL,
    identifier character varying(255),
    status character varying(255),
    battery_level integer,
    zone bigint,
    speed_limit integer,
    CONSTRAINT scooters_status_check CHECK (((status)::text = ANY (ARRAY[('Active'::character varying)::text, ('Inactive'::character varying)::text, ('Blocked'::character varying)::text, ('Unblocked'::character varying)::text, ('Broken'::character varying)::text, ('Rented'::character varying)::text])))
);


ALTER TABLE public.scooters OWNER TO root;

--
-- Name: COLUMN scooters.identifier; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.scooters.identifier IS 'Unique identifier';


--
-- Name: COLUMN scooters.zone; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.scooters.zone IS 'Location of the scooter';


--
-- Name: scooters_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.scooters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.scooters_id_seq OWNER TO root;

--
-- Name: scooters_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.scooters_id_seq OWNED BY public.scooters.id;


--
-- Name: tasks; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.tasks (
    order_id bigint NOT NULL,
    scooter_id bigint NOT NULL,
    priority integer NOT NULL,
    comment character varying(255)
);


ALTER TABLE public.tasks OWNER TO root;

--
-- Name: TABLE tasks; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON TABLE public.tasks IS 'Intermediate table holding records linking Orders and Scooters';


--
-- Name: COLUMN tasks.order_id; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.tasks.order_id IS 'link to orders';


--
-- Name: COLUMN tasks.scooter_id; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.tasks.scooter_id IS 'link to scooters';


--
-- Name: COLUMN tasks.priority; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.tasks.priority IS '1 - highest priority';


--
-- Name: COLUMN tasks.comment; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.tasks.comment IS 'Description for completed or canceled task';


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_roles OWNER TO root;

--
-- Name: TABLE user_roles; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON TABLE public.user_roles IS 'Intermediate table holding records linking User and Roles';


--
-- Name: users; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    first_name character varying(60) NOT NULL,
    last_name character varying(60),
    password character varying(60),
    phone_number character varying(50) NOT NULL,
    gender character varying(20),
    date_of_birth date,
    zone bigint,
    shift character varying(50),
    created_by bigint,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    user_manager bigint,
    CONSTRAINT users_gender_check CHECK (((gender)::text = ANY (ARRAY['Male'::text, 'Female'::text]))),
    CONSTRAINT users_shift_check CHECK (((shift)::text = ANY (ARRAY['day'::text, 'night'::text])))
);


ALTER TABLE public.users OWNER TO root;

--
-- Name: COLUMN users.password; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.password IS 'Should remain blank during the creation phase and will be defined upon the user''s first login.';


--
-- Name: COLUMN users.phone_number; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.phone_number IS 'Unique user identifier';


--
-- Name: COLUMN users.gender; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.gender IS 'Allowed values:
Male
Female
Not specified';


--
-- Name: COLUMN users.zone; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.zone IS 'predefined available zone';


--
-- Name: COLUMN users.shift; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.shift IS 'Allowed values:
Day_shift
Night_shift';


--
-- Name: COLUMN users.created_by; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.created_by IS 'Creator of this record';


--
-- Name: COLUMN users.created_at; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.created_at IS 'Date of record creation';


--
-- Name: COLUMN users.user_manager; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.user_manager IS 'Manager of this user';


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO root;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: zones; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.zones (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.zones OWNER TO root;

--
-- Name: COLUMN zones.name; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.zones.name IS 'Predefined zone';


--
-- Name: zones_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.zones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.zones_id_seq OWNER TO root;

--
-- Name: zones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.zones_id_seq OWNED BY public.zones.id;


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- Name: scooters id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.scooters ALTER COLUMN id SET DEFAULT nextval('public.scooters_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: zones id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.zones ALTER COLUMN id SET DEFAULT nextval('public.zones_id_seq'::regclass);


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.notifications VALUES (6, 314, '2024-09-19 16:28:08.084071+04', 49, 1, 'approved');


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.orders VALUES (49, 'Move', 'order12', NULL, 3, '2024-09-01 15:38:38.306416+04', 3, '2024-09-01 15:46:24.682307+04', 6, 'Assigned', NULL, NULL);
INSERT INTO public.orders VALUES (51, 'Move', 'order13', NULL, 3, '2024-09-01 19:11:47.500381+04', NULL, NULL, 6, 'Assigned', NULL, NULL);
INSERT INTO public.orders VALUES (52, 'Move', 'order15', NULL, 3, '2024-09-01 19:17:24.501949+04', NULL, NULL, 6, 'Assigned', NULL, NULL);
INSERT INTO public.orders VALUES (19, 'Charge', 'order1', 'test-order', 3, '2024-07-26 16:23:47.872315+04', NULL, NULL, 6, 'Created', NULL, NULL);
INSERT INTO public.orders VALUES (54, 'Move', 'order26', 'test-order', 3, '2024-09-01 19:39:01.645554+04', 3, '2024-09-20 21:21:49.845288+04', 6, 'Assigned', NULL, NULL);


--
-- Data for Name: refreshtoken; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.refreshtoken VALUES (17, '2024-08-17 22:22:06.535785+04', '0dc2645d-ac93-4433-a0a8-38e12b488a31', 25);
INSERT INTO public.refreshtoken VALUES (11, '2024-09-17 20:08:05.278574+04', '86b14380-be34-4a37-8dfc-20b4f611e811', 26);
INSERT INTO public.refreshtoken VALUES (12, '2024-08-01 17:38:23.737976+04', '074e54ba-374b-483a-b69b-e205a8f67952', 29);
INSERT INTO public.refreshtoken VALUES (16, '2024-08-21 11:13:39.575383+04', '9afde03e-eaf8-422f-a6a8-967744581ef9', 39);
INSERT INTO public.refreshtoken VALUES (9, '2024-09-24 11:27:00.996156+04', 'adac2024-d44a-49da-9052-5b0322584719', 6);
INSERT INTO public.refreshtoken VALUES (22, '2024-09-15 17:41:09.893009+04', '2b625703-ec45-405b-8590-a3f7a4eb7fbb', 533);


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.roles VALUES (1, 'Admin');
INSERT INTO public.roles VALUES (2, 'Manager');
INSERT INTO public.roles VALUES (3, 'Supervisor');
INSERT INTO public.roles VALUES (4, 'Charger');
INSERT INTO public.roles VALUES (5, 'Scout');


--
-- Data for Name: scooters; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.scooters VALUES (3, '3388', 'Blocked', 20, 1, 30);
INSERT INTO public.scooters VALUES (1, '3366', 'Active', 40, 1, 50);
INSERT INTO public.scooters VALUES (2, '3367', 'Active', 80, 2, 45);
INSERT INTO public.scooters VALUES (4, '4466', 'Active', 40, 3, 100);
INSERT INTO public.scooters VALUES (5, '4444', 'Active', 40, 1, 50);
INSERT INTO public.scooters VALUES (6, '4455', 'Active', 40, 2, 50);
INSERT INTO public.scooters VALUES (7, NULL, NULL, NULL, NULL, NULL);


--
-- Data for Name: tasks; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.tasks VALUES (49, 3, 1, NULL);
INSERT INTO public.tasks VALUES (49, 1, -1, 'Scooter not found in place
Manager approved your decision.');
INSERT INTO public.tasks VALUES (19, 3, 1, NULL);
INSERT INTO public.tasks VALUES (51, 1, 1, NULL);
INSERT INTO public.tasks VALUES (51, 2, 2, NULL);
INSERT INTO public.tasks VALUES (52, 1, 1, NULL);
INSERT INTO public.tasks VALUES (52, 2, 2, NULL);
INSERT INTO public.tasks VALUES (54, 2, 2, NULL);
INSERT INTO public.tasks VALUES (54, 3, 1, NULL);
INSERT INTO public.tasks VALUES (19, 1, 2, NULL);
INSERT INTO public.tasks VALUES (54, 1, 2, NULL);


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.user_roles VALUES (3, 1);
INSERT INTO public.user_roles VALUES (6, 5);
INSERT INTO public.user_roles VALUES (533, 4);
INSERT INTO public.user_roles VALUES (25, 4);
INSERT INTO public.user_roles VALUES (25, 5);
INSERT INTO public.user_roles VALUES (26, 2);
INSERT INTO public.user_roles VALUES (27, 1);
INSERT INTO public.user_roles VALUES (29, 4);
INSERT INTO public.user_roles VALUES (29, 5);
INSERT INTO public.user_roles VALUES (39, 3);
INSERT INTO public.user_roles VALUES (39, 4);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.users VALUES (6, 'Scout', 'Smith', '$2a$10$Rj4uSPNVaHiEf338Ft3xIe9jfOF5xqRYKPJaVAi3evpU1b/BHZP8e', '71001122', 'Female', NULL, 3, 'night', 3, '2024-07-17 20:51:43.468797+04', 3);
INSERT INTO public.users VALUES (533, 'Charger', NULL, '$2a$10$fkTiCdFaHU51ullnuHNpW.UCdW9t9FomYXJEJH7FIbOBIPI1zdjbG', '33001122', 'Male', NULL, NULL, NULL, 3, '2024-09-14 17:35:52.3373+04', 26);
INSERT INTO public.users VALUES (29, 'user_scout', NULL, '$2a$10$Ziiq1DJCCNk3yYqEAUZTPuAxkigvWeWKo/oBnZPKYSLc1bWAc0nrq', '95001122', NULL, NULL, NULL, NULL, NULL, NULL, 3);
INSERT INTO public.users VALUES (3, 'duty_admin', 'Blinken', '$2a$10$Ep0QyL0tmsYQpQ02gZcfgOVmDxUxnybNiloAmgnNcG5iQ2T38hIsS', '55001122', 'Female', '2024-07-16', 2, 'day', 6, '2024-07-16 21:49:22.325+04', NULL);
INSERT INTO public.users VALUES (25, 'user_scout', NULL, '$2a$10$Hj6VjoSlmyYD/Gqjjb.nhupngoGqZR2I4NmPn1KQ2HBHHN8Clwe/2', '94001122', 'Male', NULL, 1, NULL, 3, '2024-07-27 14:47:43.379171+04', 3);
INSERT INTO public.users VALUES (26, 'manager', NULL, '$2a$10$0xDTxLnPWk972eEX9UJfJObssahQMNrFYjXmlPy6ojYPGWoRyE1h.', '77553311', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.users VALUES (39, 'Supervisor', NULL, '$2a$10$atYUerVtgumF/tL9Zy7iR.X0ExqfLaLIhRAfzhdgF.gf2alEXr11G', '73001122', 'Male', NULL, 1, NULL, 3, '2024-08-02 20:34:58.935859+04', NULL);
INSERT INTO public.users VALUES (27, 'admin', NULL, '$2a$10$WZXVlLM/q7jKNwZNy8gSIesnCZEI.SUgSk34IDQFvEVdKfAB95RwK', '56001122', NULL, NULL, NULL, NULL, NULL, NULL, NULL);


--
-- Data for Name: zones; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.zones VALUES (1, 'z01');
INSERT INTO public.zones VALUES (2, 'z02');
INSERT INTO public.zones VALUES (3, 'z03');


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.notifications_id_seq', 486, true);


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.orders_id_seq', 232, true);


--
-- Name: refreshtoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.refreshtoken_id_seq', 26, true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.roles_id_seq', 5, true);


--
-- Name: scooters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.scooters_id_seq', 7, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.users_id_seq', 803, true);


--
-- Name: zones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.zones_id_seq', 1, false);


--
-- Name: tasks PK_Task; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT "PK_Task" PRIMARY KEY (order_id, scooter_id);


--
-- Name: user_roles PK_UserRole; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "PK_UserRole" PRIMARY KEY (user_id, role_id);


--
-- Name: roles PK_roles; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT "PK_roles" PRIMARY KEY (id);


--
-- Name: notifications notifications_pk; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pk PRIMARY KEY (id);


--
-- Name: notifications notifications_unique; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_unique UNIQUE (order_id, scooter_id);


--
-- Name: orders orders_name_unique; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_name_unique UNIQUE (name);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: refreshtoken refresh_pk; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_pk PRIMARY KEY (id);


--
-- Name: refreshtoken refresh_unique_token; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_unique_token UNIQUE (current_token);


--
-- Name: refreshtoken refresh_unique_user_id; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_unique_user_id UNIQUE (user_id);


--
-- Name: roles roles_name_unique; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_unique UNIQUE (name);


--
-- Name: scooters scooters_identifier_unique; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_identifier_unique UNIQUE (identifier);


--
-- Name: scooters scooters_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_pkey PRIMARY KEY (id);


--
-- Name: users users_phone_number_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_number_key UNIQUE (phone_number);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: zones zones_name unique; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.zones
    ADD CONSTRAINT "zones_name unique" UNIQUE (name);


--
-- Name: zones zones_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.zones
    ADD CONSTRAINT zones_pkey PRIMARY KEY (id);


--
-- Name: tasks FK_Task_Order_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT "FK_Task_Order_Id" FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;


--
-- Name: tasks FK_Task_Scooter_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT "FK_Task_Scooter_Id" FOREIGN KEY (scooter_id) REFERENCES public.scooters(id) ON DELETE CASCADE;


--
-- Name: user_roles FK_UserRole_Role_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_Role_Id" FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: user_roles FK_UserRole_User_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_User_Id" FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: refreshtoken FK_refreshtoken_user_id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT "FK_refreshtoken_user_id" FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: notifications notifications_tasks_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_tasks_fk FOREIGN KEY (order_id, scooter_id) REFERENCES public.tasks(order_id, scooter_id) ON DELETE CASCADE;


--
-- Name: notifications notifications_users_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: orders orders_assigned_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: orders orders_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: orders orders_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: scooters scooters_zones_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_zones_fk FOREIGN KEY (zone) REFERENCES public.zones(id);


--
-- Name: users user_head_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_head_fk FOREIGN KEY (user_manager) REFERENCES public.users(id) ON DELETE SET DEFAULT;


--
-- Name: users users_users_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_users_fk FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET DEFAULT;


--
-- Name: users users_zones_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_zones_fk FOREIGN KEY (zone) REFERENCES public.zones(id);


--
-- PostgreSQL database dump complete
--

