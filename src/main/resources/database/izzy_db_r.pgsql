--
-- PostgreSQL database dump
--

-- Dumped from database version 12.19 (Ubuntu 12.19-0ubuntu0.20.04.1)
-- Dumped by pg_dump version 12.19 (Ubuntu 12.19-0ubuntu0.20.04.1)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: order_scooter; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.order_scooter (
    "order_Id" bigint NOT NULL,
    "scooter_Id" bigint NOT NULL,
    "Priority" integer
);


ALTER TABLE public.order_scooter OWNER TO root;

--
-- Name: TABLE order_scooter; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON TABLE public.order_scooter IS 'Intermediate table holding records linking Orders and Scooters';


--
-- Name: COLUMN order_scooter."order_Id"; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.order_scooter."order_Id" IS 'link to orders';


--
-- Name: COLUMN order_scooter."scooter_Id"; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.order_scooter."scooter_Id" IS 'link to scooters';


--
-- Name: orders; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.orders (
    id bigint NOT NULL,
    action character varying(255),
    name character varying(255),
    description character varying(255),
    created_by bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_by bigint,
    updated_at timestamp without time zone,
    assigned_to bigint,
    status character varying(255),
    taken_by bigint,
    taken_at timestamp without time zone,
    done_at timestamp without time zone,
    CONSTRAINT orders_action_check CHECK (((action)::text = ANY (ARRAY[('Move'::character varying)::text, ('Charge'::character varying)::text, ('Repair'::character varying)::text]))),
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY (ARRAY[('Created'::character varying)::text, ('Assigned'::character varying)::text, ('In_Progress'::character varying)::text, ('Fulfilled'::character varying)::text, ('Canceled'::character varying)::text])))
);


ALTER TABLE public.orders OWNER TO root;

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.orders_id_seq
    AS integer
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
    expiry_date timestamp without time zone,
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
    AS integer
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
    AS integer
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
    first_name character varying(255) NOT NULL,
    last_name character varying(255),
    password character varying(60),
    phone_number character varying(255) NOT NULL,
    gender character varying(255),
    date_of_birth date,
    zone bigint,
    shift character varying(255),
    created_by bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    head_for_user bigint,
    CONSTRAINT users_gender_check CHECK (((gender)::text = ANY (ARRAY[('Male'::character varying)::text, ('Female'::character varying)::text, ('Not specified'::character varying)::text]))),
    CONSTRAINT users_shift_check CHECK (((shift)::text = ANY (ARRAY[('day shift'::character varying)::text, ('night shift'::character varying)::text])))
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

COMMENT ON COLUMN public.users.zone IS 'id to predefined available zones';


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
-- Name: COLUMN users.head_for_user; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.users.head_for_user IS 'Manager of this user';


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.users_id_seq
    AS integer
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
    AS integer
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
-- Data for Name: order_scooter; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.order_scooter ("order_Id", "scooter_Id", "Priority") FROM stdin;
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.orders (id, action, name, description, created_by, created_at, updated_by, updated_at, assigned_to, status, taken_by, taken_at, done_at) FROM stdin;
\.


--
-- Data for Name: refreshtoken; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.refreshtoken (id, expiry_date, current_token, user_id) FROM stdin;
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.roles (id, name) FROM stdin;
1	Admin
2	Manager
3	Supervisor
4	Charger
5	Scout
\.


--
-- Data for Name: scooters; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.scooters (id, identifier, status, battery_level, zone, speed_limit) FROM stdin;
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.user_roles (user_id, role_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.users (id, first_name, last_name, password, phone_number, gender, date_of_birth, zone, shift, created_by, created_at, head_for_user) FROM stdin;
\.


--
-- Data for Name: zones; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.zones (id, name) FROM stdin;
\.


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.orders_id_seq', 1, false);


--
-- Name: refreshtoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.refreshtoken_id_seq', 1, false);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.roles_id_seq', 5, true);


--
-- Name: scooters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.scooters_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


--
-- Name: zones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.zones_id_seq', 1, false);


--
-- Name: order_scooter PK_OrderScooter; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "PK_OrderScooter" PRIMARY KEY ("order_Id", "scooter_Id");


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
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: orders orders_unique; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_unique UNIQUE (name);


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
-- Name: order_scooter FK_OrderScooter_Order_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "FK_OrderScooter_Order_Id" FOREIGN KEY ("order_Id") REFERENCES public.orders(id);


--
-- Name: order_scooter FK_OrderScooter_Scooter_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "FK_OrderScooter_Scooter_Id" FOREIGN KEY ("scooter_Id") REFERENCES public.scooters(id);


--
-- Name: user_roles FK_UserRole_Role_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_Role_Id" FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: user_roles FK_UserRole_User_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_User_Id" FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: refreshtoken FK_refreshtoken_user_id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT "FK_refreshtoken_user_id" FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: users head_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT head_fk FOREIGN KEY (head_for_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: orders orders_assigned_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id);


--
-- Name: orders orders_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: orders orders_taken_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_taken_by_fkey FOREIGN KEY (taken_by) REFERENCES public.users(id);


--
-- Name: orders orders_updated_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: scooters scooters_zones_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_zones_fk FOREIGN KEY (zone) REFERENCES public.zones(id);


--
-- Name: users users_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: users zone_fk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT zone_fk FOREIGN KEY (zone) REFERENCES public.zones(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

