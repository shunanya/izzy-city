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
    order_id bigint NOT NULL,
    scooter_id bigint NOT NULL,
    priority integer,
    comment character varying(255)
);


ALTER TABLE public.order_scooter OWNER TO root;

--
-- Name: TABLE order_scooter; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON TABLE public.order_scooter IS 'Intermediate table holding records linking Orders and Scooters';


--
-- Name: COLUMN order_scooter.order_id; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.order_scooter.order_id IS 'link to orders';


--
-- Name: COLUMN order_scooter.scooter_id; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.order_scooter.scooter_id IS 'link to scooters';


--
-- Name: COLUMN order_scooter.priority; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.order_scooter.priority IS '1 - highest priority';


--
-- Name: COLUMN order_scooter.comment; Type: COMMENT; Schema: public; Owner: root
--

COMMENT ON COLUMN public.order_scooter.comment IS 'Description for completed or canceled task';


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

INSERT INTO public.order_scooter VALUES (19, 1, 2, NULL);
INSERT INTO public.order_scooter VALUES (19, 3, 1, NULL);
INSERT INTO public.order_scooter VALUES (19, 2, 3, NULL);
INSERT INTO public.order_scooter VALUES (41, 1, 1, NULL);
INSERT INTO public.order_scooter VALUES (41, 3, -1, 'CANCELED');
INSERT INTO public.order_scooter VALUES (41, 5, 2, NULL);
INSERT INTO public.order_scooter VALUES (41, 4, 0, 'COMPLETED');


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.orders VALUES (19, 'Charge', 'order1', 'test-order', 3, '2024-07-26 16:23:47.872315+04', NULL, NULL, 6, 'Created', NULL, NULL);
INSERT INTO public.orders VALUES (41, 'Move', 'order11', NULL, 3, '2024-08-15 13:12:58.359906+04', 3, '2024-08-15 18:35:33.250483+04', 6, 'Created', NULL, NULL);
INSERT INTO public.orders VALUES (15, 'Move', 'order2', NULL, 6, '2024-07-22 15:38:03.509133+04', NULL, NULL, 28, 'Created', NULL, NULL);


--
-- Data for Name: refreshtoken; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.refreshtoken VALUES (17, '2024-08-17 22:22:06.535785+04', '0dc2645d-ac93-4433-a0a8-38e12b488a31', 25);
INSERT INTO public.refreshtoken VALUES (9, '2024-08-18 12:04:45.488856+04', '9eb088e5-60ef-42b2-b47f-e82646d09294', 6);
INSERT INTO public.refreshtoken VALUES (11, '2024-08-18 20:48:35.148349+04', '9d0c7729-d312-49a4-ac9f-184ac0f15f71', 26);
INSERT INTO public.refreshtoken VALUES (14, '2024-08-18 20:49:44.25124+04', '5f46564a-58e3-4f50-a190-c6d912c91c16', 3);
INSERT INTO public.refreshtoken VALUES (12, '2024-08-01 17:38:23.737976+04', '074e54ba-374b-483a-b69b-e205a8f67952', 29);
INSERT INTO public.refreshtoken VALUES (16, '2024-08-17 14:55:32.173243+04', '0084c24f-d075-419c-8f91-65b6f3df65e2', 39);


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


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.user_roles VALUES (3, 1);
INSERT INTO public.user_roles VALUES (6, 5);
INSERT INTO public.user_roles VALUES (25, 4);
INSERT INTO public.user_roles VALUES (25, 5);
INSERT INTO public.user_roles VALUES (26, 2);
INSERT INTO public.user_roles VALUES (27, 1);
INSERT INTO public.user_roles VALUES (28, 5);
INSERT INTO public.user_roles VALUES (28, 4);
INSERT INTO public.user_roles VALUES (29, 4);
INSERT INTO public.user_roles VALUES (29, 5);
INSERT INTO public.user_roles VALUES (39, 3);
INSERT INTO public.user_roles VALUES (39, 4);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.users VALUES (26, 'manager', NULL, '$2a$10$0xDTxLnPWk972eEX9UJfJObssahQMNrFYjXmlPy6ojYPGWoRyE1h.', '77553311', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.users VALUES (27, 'admin', NULL, '$2a$10$WZXVlLM/q7jKNwZNy8gSIesnCZEI.SUgSk34IDQFvEVdKfAB95RwK', '56001122', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.users VALUES (28, 'user_scout', NULL, '$2a$10$v9iqzXU/RqobG.zTP2kk3ustMFM3N11N9l14d904cWRtGa/sbTMRi', '99001122', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.users VALUES (29, 'user_scout', NULL, '$2a$10$Ziiq1DJCCNk3yYqEAUZTPuAxkigvWeWKo/oBnZPKYSLc1bWAc0nrq', '95001122', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.users VALUES (3, 'duty_admin', NULL, '$2a$10$KDr0088bx1wGYDXuRT.YL.5Xz5spiagqCiuFPiE.o0TZst7b3nudK', '55001122', 'Female', '2024-07-16', 2, 'day shift', 6, '2024-07-16 21:49:22.325+04', NULL);
INSERT INTO public.users VALUES (6, 'Scout', 'Smith', '$2a$10$Vu1UO5EpZptJJSCY2qQOMe9won4/A07UnT74EHgIdxUhrcN2lc8Z.', '71001122', 'Female', NULL, 3, 'night shift', 3, '2024-07-17 20:51:43.468797+04', NULL);
INSERT INTO public.users VALUES (39, 'Supervisor', NULL, '$2a$10$atYUerVtgumF/tL9Zy7iR.X0ExqfLaLIhRAfzhdgF.gf2alEXr11G', '73001122', 'Male', NULL, 1, NULL, 3, '2024-08-02 20:34:58.935859+04', NULL);
INSERT INTO public.users VALUES (25, 'user_scout', NULL, '$2a$10$Hj6VjoSlmyYD/Gqjjb.nhupngoGqZR2I4NmPn1KQ2HBHHN8Clwe/2', '94001122', 'Male', NULL, 1, NULL, 3, '2024-07-27 14:47:43.379171+04', NULL);


--
-- Data for Name: zones; Type: TABLE DATA; Schema: public; Owner: root
--

INSERT INTO public.zones VALUES (1, 'z01');
INSERT INTO public.zones VALUES (2, 'z02');
INSERT INTO public.zones VALUES (3, 'z03');


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.orders_id_seq', 41, true);


--
-- Name: refreshtoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.refreshtoken_id_seq', 17, true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.roles_id_seq', 5, true);


--
-- Name: scooters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.scooters_id_seq', 6, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.users_id_seq', 39, true);


--
-- Name: zones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.zones_id_seq', 1, false);


--
-- Name: order_scooter PK_OrderScooter; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "PK_OrderScooter" PRIMARY KEY (order_id, scooter_id);


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
    ADD CONSTRAINT "FK_OrderScooter_Order_Id" FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;


--
-- Name: order_scooter FK_OrderScooter_Scooter_Id; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "FK_OrderScooter_Scooter_Id" FOREIGN KEY (scooter_id) REFERENCES public.scooters(id) ON DELETE CASCADE;


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
    ADD CONSTRAINT user_head_fk FOREIGN KEY (head_for_user) REFERENCES public.users(id) ON DELETE SET DEFAULT;


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

