PGDMP                          |            izzy %   12.19 (Ubuntu 12.19-0ubuntu0.20.04.1) %   12.19 (Ubuntu 12.19-0ubuntu0.20.04.1) _               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16748    izzy    DATABASE     v   CREATE DATABASE izzy WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';
    DROP DATABASE izzy;
                root    false            �            1259    17042    order_scooter    TABLE     �   CREATE TABLE public.order_scooter (
    "order_Id" bigint NOT NULL,
    "scooter_Id" bigint NOT NULL,
    "Priority" integer
);
 !   DROP TABLE public.order_scooter;
       public         heap    root    false                       0    0    TABLE order_scooter    COMMENT     k   COMMENT ON TABLE public.order_scooter IS 'Intermediate table holding records linking Orders and Scooters';
          public          root    false    209                       0    0    COLUMN order_scooter."order_Id"    COMMENT     G   COMMENT ON COLUMN public.order_scooter."order_Id" IS 'link to orders';
          public          root    false    209                       0    0 !   COLUMN order_scooter."scooter_Id"    COMMENT     K   COMMENT ON COLUMN public.order_scooter."scooter_Id" IS 'link to scooters';
          public          root    false    209            �            1259    16871    orders    TABLE     �  CREATE TABLE public.orders (
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
    DROP TABLE public.orders;
       public         heap    root    false            �            1259    16869    orders_id_seq    SEQUENCE     �   CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.orders_id_seq;
       public          root    false    207                       0    0    orders_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;
          public          root    false    206            �            1259    17142    refreshtoken_id_seq    SEQUENCE     |   CREATE SEQUENCE public.refreshtoken_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.refreshtoken_id_seq;
       public          root    false            �            1259    17144    refreshtoken    TABLE     �   CREATE TABLE public.refreshtoken (
    id bigint DEFAULT nextval('public.refreshtoken_id_seq'::regclass) NOT NULL,
    expiry_date timestamp without time zone,
    current_token character varying(255),
    user_id bigint NOT NULL
);
     DROP TABLE public.refreshtoken;
       public         heap    root    false    214                       0    0    TABLE refreshtoken    COMMENT     L   COMMENT ON TABLE public.refreshtoken IS 'information on the refresh token';
          public          root    false    215                       0    0 !   COLUMN refreshtoken.current_token    COMMENT     P   COMMENT ON COLUMN public.refreshtoken.current_token IS 'current refresh token';
          public          root    false    215                       0    0    COLUMN refreshtoken.user_id    COMMENT     G   COMMENT ON COLUMN public.refreshtoken.user_id IS 'owner of the token';
          public          root    false    215            �            1259    17072    roles    TABLE     c  CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY (ARRAY[('Admin'::character varying)::text, ('Manager'::character varying)::text, ('Supervisor'::character varying)::text, ('Charger'::character varying)::text, ('Scout'::character varying)::text])))
);
    DROP TABLE public.roles;
       public         heap    root    false            �            1259    17095    roles_id_seq    SEQUENCE     �   CREATE SEQUENCE public.roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.roles_id_seq;
       public          root    false    211                       0    0    roles_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;
          public          root    false    212            �            1259    16842    scooters    TABLE       CREATE TABLE public.scooters (
    id bigint NOT NULL,
    identifier character varying(255),
    status character varying(255),
    battery_level integer,
    zone integer,
    speed_limit integer,
    zone_id bigint,
    CONSTRAINT scooters_status_check CHECK (((status)::text = ANY (ARRAY[('Active'::character varying)::text, ('Inactive'::character varying)::text, ('Blocked'::character varying)::text, ('Unblocked'::character varying)::text, ('Broken'::character varying)::text, ('Rented'::character varying)::text])))
);
    DROP TABLE public.scooters;
       public         heap    root    false                       0    0    COLUMN scooters.identifier    COMMENT     E   COMMENT ON COLUMN public.scooters.identifier IS 'Unique identifier';
          public          root    false    205                       0    0    COLUMN scooters.zone    COMMENT     E   COMMENT ON COLUMN public.scooters.zone IS 'Location of the scooter';
          public          root    false    205            �            1259    16840    scooters_id_seq    SEQUENCE     �   CREATE SEQUENCE public.scooters_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.scooters_id_seq;
       public          root    false    205                       0    0    scooters_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.scooters_id_seq OWNED BY public.scooters.id;
          public          root    false    204            �            1259    17057 
   user_roles    TABLE     ]   CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);
    DROP TABLE public.user_roles;
       public         heap    root    false                       0    0    TABLE user_roles    COMMENT     c   COMMENT ON TABLE public.user_roles IS 'Intermediate table holding records linking User and Roles';
          public          root    false    210            �            1259    16808    users    TABLE     4  CREATE TABLE public.users (
    id bigint NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255),
    password character varying(60),
    phone_number character varying(255) NOT NULL,
    gender character varying(255),
    date_of_birth date,
    zone integer,
    shift character varying(255),
    created_by bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    head_for_user bigint,
    zone_id bigint,
    CONSTRAINT users_gender_check CHECK (((gender)::text = ANY (ARRAY[('Male'::character varying)::text, ('Female'::character varying)::text, ('Not specified'::character varying)::text]))),
    CONSTRAINT users_shift_check CHECK (((shift)::text = ANY (ARRAY[('day shift'::character varying)::text, ('night shift'::character varying)::text])))
);
    DROP TABLE public.users;
       public         heap    root    false                       0    0    COLUMN users.password    COMMENT     �   COMMENT ON COLUMN public.users.password IS 'Should remain blank during the creation phase and will be defined upon the user''s first login.';
          public          root    false    203                       0    0    COLUMN users.phone_number    COMMENT     I   COMMENT ON COLUMN public.users.phone_number IS 'Unique user identifier';
          public          root    false    203                       0    0    COLUMN users.gender    COMMENT     V   COMMENT ON COLUMN public.users.gender IS 'Allowed values:
Male
Female
Not specified';
          public          root    false    203                       0    0    COLUMN users.zone    COMMENT     K   COMMENT ON COLUMN public.users.zone IS 'id to predefined available zones';
          public          root    false    203                        0    0    COLUMN users.shift    COMMENT     Q   COMMENT ON COLUMN public.users.shift IS 'Allowed values:
Day_shift
Night_shift';
          public          root    false    203            !           0    0    COLUMN users.created_by    COMMENT     G   COMMENT ON COLUMN public.users.created_by IS 'Creator of this record';
          public          root    false    203            "           0    0    COLUMN users.created_at    COMMENT     H   COMMENT ON COLUMN public.users.created_at IS 'Date of record creation';
          public          root    false    203            #           0    0    COLUMN users.head_for_user    COMMENT     H   COMMENT ON COLUMN public.users.head_for_user IS 'Manager of this user';
          public          root    false    203            �            1259    16806    users_id_seq    SEQUENCE     �   CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.users_id_seq;
       public          root    false    203            $           0    0    users_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
          public          root    false    202            �            1259    16916    zones    TABLE     W   CREATE TABLE public.zones (
    id bigint NOT NULL,
    name character varying(255)
);
    DROP TABLE public.zones;
       public         heap    root    false            %           0    0    COLUMN zones.name    COMMENT     :   COMMENT ON COLUMN public.zones.name IS 'Predefined zone';
          public          root    false    208            �            1259    17097    zones_id_seq    SEQUENCE     �   CREATE SEQUENCE public.zones_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.zones_id_seq;
       public          root    false    208            &           0    0    zones_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.zones_id_seq OWNED BY public.zones.id;
          public          root    false    213            @           2604    17167 	   orders id    DEFAULT     f   ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);
 8   ALTER TABLE public.orders ALTER COLUMN id DROP DEFAULT;
       public          root    false    206    207    207            D           2604    17280    roles id    DEFAULT     d   ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);
 7   ALTER TABLE public.roles ALTER COLUMN id DROP DEFAULT;
       public          root    false    212    211            =           2604    17304    scooters id    DEFAULT     j   ALTER TABLE ONLY public.scooters ALTER COLUMN id SET DEFAULT nextval('public.scooters_id_seq'::regclass);
 :   ALTER TABLE public.scooters ALTER COLUMN id DROP DEFAULT;
       public          root    false    205    204    205            :           2604    17350    users id    DEFAULT     d   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public          root    false    202    203    203            C           2604    17562    zones id    DEFAULT     d   ALTER TABLE ONLY public.zones ALTER COLUMN id SET DEFAULT nextval('public.zones_id_seq'::regclass);
 7   ALTER TABLE public.zones ALTER COLUMN id DROP DEFAULT;
       public          root    false    213    208                      0    17042    order_scooter 
   TABLE DATA           M   COPY public.order_scooter ("order_Id", "scooter_Id", "Priority") FROM stdin;
    public          root    false    209   �p                 0    16871    orders 
   TABLE DATA           �   COPY public.orders (id, action, name, description, created_by, created_at, updated_by, updated_at, assigned_to, status, taken_by, taken_at, done_at) FROM stdin;
    public          root    false    207   �p       	          0    17144    refreshtoken 
   TABLE DATA           O   COPY public.refreshtoken (id, expiry_date, current_token, user_id) FROM stdin;
    public          root    false    215   �p                 0    17072    roles 
   TABLE DATA           )   COPY public.roles (id, name) FROM stdin;
    public          root    false    211   �p       �          0    16842    scooters 
   TABLE DATA           e   COPY public.scooters (id, identifier, status, battery_level, zone, speed_limit, zone_id) FROM stdin;
    public          root    false    205   %q                 0    17057 
   user_roles 
   TABLE DATA           6   COPY public.user_roles (user_id, role_id) FROM stdin;
    public          root    false    210   Bq       �          0    16808    users 
   TABLE DATA           �   COPY public.users (id, first_name, last_name, password, phone_number, gender, date_of_birth, zone, shift, created_by, created_at, head_for_user, zone_id) FROM stdin;
    public          root    false    203   _q                 0    16916    zones 
   TABLE DATA           )   COPY public.zones (id, name) FROM stdin;
    public          root    false    208   |q       '           0    0    orders_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.orders_id_seq', 1, false);
          public          root    false    206            (           0    0    refreshtoken_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.refreshtoken_id_seq', 1, false);
          public          root    false    214            )           0    0    roles_id_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.roles_id_seq', 5, true);
          public          root    false    212            *           0    0    scooters_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.scooters_id_seq', 1, false);
          public          root    false    204            +           0    0    users_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.users_id_seq', 1, false);
          public          root    false    202            ,           0    0    zones_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.zones_id_seq', 1, false);
          public          root    false    213            `           2606    17526    order_scooter PK_OrderScooter 
   CONSTRAINT     s   ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "PK_OrderScooter" PRIMARY KEY ("order_Id", "scooter_Id");
 I   ALTER TABLE ONLY public.order_scooter DROP CONSTRAINT "PK_OrderScooter";
       public            root    false    209    209            b           2606    17340    user_roles PK_UserRole 
   CONSTRAINT     d   ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "PK_UserRole" PRIMARY KEY (user_id, role_id);
 B   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT "PK_UserRole";
       public            root    false    210    210            d           2606    17282    roles PK_roles 
   CONSTRAINT     N   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT "PK_roles" PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.roles DROP CONSTRAINT "PK_roles";
       public            root    false    211            T           2606    17169    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            root    false    207            V           2606    17193    orders orders_unique 
   CONSTRAINT     O   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_unique UNIQUE (name);
 >   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_unique;
       public            root    false    207            j           2606    17565    refreshtoken refresh_pk 
   CONSTRAINT     U   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_pk PRIMARY KEY (id);
 A   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT refresh_pk;
       public            root    false    215            l           2606    17260 !   refreshtoken refresh_unique_token 
   CONSTRAINT     e   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_unique_token UNIQUE (current_token);
 K   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT refresh_unique_token;
       public            root    false    215            n           2606    17268 #   refreshtoken refresh_unique_user_id 
   CONSTRAINT     a   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_unique_user_id UNIQUE (user_id);
 M   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT refresh_unique_user_id;
       public            root    false    215            f           2606    17297    roles roles_unique 
   CONSTRAINT     M   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_unique UNIQUE (name);
 <   ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_unique;
       public            root    false    211            N           2606    17306    scooters scooters_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.scooters DROP CONSTRAINT scooters_pkey;
       public            root    false    205            P           2606    17318    scooters scooters_unique 
   CONSTRAINT     Y   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_unique UNIQUE (identifier);
 B   ALTER TABLE ONLY public.scooters DROP CONSTRAINT scooters_unique;
       public            root    false    205            X           2606    17468 "   orders uk9jvsx18f8w7fjm9esi95fqosi 
   CONSTRAINT     ]   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT uk9jvsx18f8w7fjm9esi95fqosi UNIQUE (name);
 L   ALTER TABLE ONLY public.orders DROP CONSTRAINT uk9jvsx18f8w7fjm9esi95fqosi;
       public            root    false    207            H           2606    17480 !   users uk9q63snka3mdh91as4io72espi 
   CONSTRAINT     d   ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk9q63snka3mdh91as4io72espi UNIQUE (phone_number);
 K   ALTER TABLE ONLY public.users DROP CONSTRAINT uk9q63snka3mdh91as4io72espi;
       public            root    false    203            Z           2606    17482 !   zones uk9vf2c47kjchldfq92cptovfts 
   CONSTRAINT     \   ALTER TABLE ONLY public.zones
    ADD CONSTRAINT uk9vf2c47kjchldfq92cptovfts UNIQUE (name);
 K   ALTER TABLE ONLY public.zones DROP CONSTRAINT uk9vf2c47kjchldfq92cptovfts;
       public            root    false    208            h           2606    17472 !   roles ukofx66keruapi6vyqpv6f2or37 
   CONSTRAINT     \   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT ukofx66keruapi6vyqpv6f2or37 UNIQUE (name);
 K   ALTER TABLE ONLY public.roles DROP CONSTRAINT ukofx66keruapi6vyqpv6f2or37;
       public            root    false    211            R           2606    17474 $   scooters uks74tttjry78yecicp09mpbcnk 
   CONSTRAINT     e   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT uks74tttjry78yecicp09mpbcnk UNIQUE (identifier);
 N   ALTER TABLE ONLY public.scooters DROP CONSTRAINT uks74tttjry78yecicp09mpbcnk;
       public            root    false    205            J           2606    17411    users users_phone_number_key 
   CONSTRAINT     _   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_number_key UNIQUE (phone_number);
 F   ALTER TABLE ONLY public.users DROP CONSTRAINT users_phone_number_key;
       public            root    false    203            L           2606    17352    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            root    false    203            \           2606    17541    zones zones_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.zones
    ADD CONSTRAINT zones_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.zones DROP CONSTRAINT zones_pkey;
       public            root    false    208            ^           2606    17461    zones zones_unique 
   CONSTRAINT     M   ALTER TABLE ONLY public.zones
    ADD CONSTRAINT zones_unique UNIQUE (name);
 <   ALTER TABLE ONLY public.zones DROP CONSTRAINT zones_unique;
       public            root    false    208            y           2606    17516 &   order_scooter FK_OrderScooter_Order_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "FK_OrderScooter_Order_Id" FOREIGN KEY ("order_Id") REFERENCES public.orders(id);
 R   ALTER TABLE ONLY public.order_scooter DROP CONSTRAINT "FK_OrderScooter_Order_Id";
       public          root    false    2900    207    209            z           2606    17527 (   order_scooter FK_OrderScooter_Scooter_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public.order_scooter
    ADD CONSTRAINT "FK_OrderScooter_Scooter_Id" FOREIGN KEY ("scooter_Id") REFERENCES public.scooters(id);
 T   ALTER TABLE ONLY public.order_scooter DROP CONSTRAINT "FK_OrderScooter_Scooter_Id";
       public          root    false    2894    209    205            {           2606    17341    user_roles FK_UserRole_Role_Id    FK CONSTRAINT        ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_Role_Id" FOREIGN KEY (role_id) REFERENCES public.roles(id);
 J   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT "FK_UserRole_Role_Id";
       public          root    false    2916    210    211            |           2606    17388    user_roles FK_UserRole_User_Id    FK CONSTRAINT        ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_User_Id" FOREIGN KEY (user_id) REFERENCES public.users(id);
 J   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT "FK_UserRole_User_Id";
       public          root    false    203    210    2892            }           2606    17383 $   refreshtoken FK_refreshtoken_user_id    FK CONSTRAINT     �   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT "FK_refreshtoken_user_id" FOREIGN KEY (user_id) REFERENCES public.users(id);
 P   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT "FK_refreshtoken_user_id";
       public          root    false    215    203    2892            p           2606    17547 !   users fk9ldg6jk1y4l8i5btcl4litk8g    FK CONSTRAINT     �   ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk9ldg6jk1y4l8i5btcl4litk8g FOREIGN KEY (zone_id) REFERENCES public.zones(id);
 K   ALTER TABLE ONLY public.users DROP CONSTRAINT fk9ldg6jk1y4l8i5btcl4litk8g;
       public          root    false    2908    208    203            t           2606    17557 $   scooters fka775m9wsrexvvamc838wfxwys    FK CONSTRAINT     �   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT fka775m9wsrexvvamc838wfxwys FOREIGN KEY (zone_id) REFERENCES public.zones(id);
 N   ALTER TABLE ONLY public.scooters DROP CONSTRAINT fka775m9wsrexvvamc838wfxwys;
       public          root    false    208    2908    205            r           2606    17426    users head_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.users
    ADD CONSTRAINT head_fk FOREIGN KEY (head_for_user) REFERENCES public.users(id) ON DELETE CASCADE;
 7   ALTER TABLE ONLY public.users DROP CONSTRAINT head_fk;
       public          root    false    203    2892    203            u           2606    17363    orders orders_assigned_to_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id);
 H   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_assigned_to_fkey;
       public          root    false    203    207    2892            v           2606    17368    orders orders_created_by_fkey    FK CONSTRAINT        ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_created_by_fkey;
       public          root    false    2892    207    203            w           2606    17373    orders orders_taken_by_fkey    FK CONSTRAINT     {   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_taken_by_fkey FOREIGN KEY (taken_by) REFERENCES public.users(id);
 E   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_taken_by_fkey;
       public          root    false    203    207    2892            x           2606    17378    orders orders_updated_by_fkey    FK CONSTRAINT        ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id);
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_updated_by_fkey;
       public          root    false    207    2892    203            s           2606    17552    scooters scooters_zones_fk    FK CONSTRAINT     v   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_zones_fk FOREIGN KEY (zone) REFERENCES public.zones(id);
 D   ALTER TABLE ONLY public.scooters DROP CONSTRAINT scooters_zones_fk;
       public          root    false    208    2908    205            q           2606    17413    users users_created_by_fkey    FK CONSTRAINT     }   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);
 E   ALTER TABLE ONLY public.users DROP CONSTRAINT users_created_by_fkey;
       public          root    false    2892    203    203            o           2606    17542    users zone_fk    FK CONSTRAINT     {   ALTER TABLE ONLY public.users
    ADD CONSTRAINT zone_fk FOREIGN KEY (zone) REFERENCES public.zones(id) ON DELETE CASCADE;
 7   ALTER TABLE ONLY public.users DROP CONSTRAINT zone_fk;
       public          root    false    2908    208    203                  x������ � �            x������ � �      	      x������ � �         <   x�3�tL����2��M�KLO-�2�.-H-*�,�/�2�t�H,	�r'痖p��qqq �O�      �      x������ � �            x������ � �      �      x������ � �            x������ � �     