PGDMP         5            
    |            izzy %   12.20 (Ubuntu 12.20-0ubuntu0.20.04.1) %   12.20 (Ubuntu 12.20-0ubuntu0.20.04.1) m               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                        0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            !           1262    16748    izzy    DATABASE     v   CREATE DATABASE izzy WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';
    DROP DATABASE izzy;
                root    false            �            1259    18189    history    TABLE     �   CREATE TABLE public.history (
    id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0),
    type character varying,
    action character varying,
    description character varying,
    user_id bigint
);
    DROP TABLE public.history;
       public         heap    root    false            �            1259    18192    history_id_seq    SEQUENCE     w   CREATE SEQUENCE public.history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.history_id_seq;
       public          root    false    218            "           0    0    history_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.history_id_seq OWNED BY public.history.id;
          public          root    false    219            �            1259    17797    notifications    TABLE     �  CREATE TABLE public.notifications (
    user_id bigint NOT NULL,
    id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP(0) NOT NULL,
    order_id bigint NOT NULL,
    scooter_id bigint NOT NULL,
    user_action character varying,
    CONSTRAINT notifications_action_check CHECK (((user_action)::text = ANY (ARRAY['approved'::text, 'rejected'::text])))
);
 !   DROP TABLE public.notifications;
       public         heap    root    false            #           0    0    COLUMN notifications.user_id    COMMENT     P   COMMENT ON COLUMN public.notifications.user_id IS 'responsible/reactable user';
          public          root    false    216            $           0    0    COLUMN notifications.created_at    COMMENT     S   COMMENT ON COLUMN public.notifications.created_at IS 'notification creation time';
          public          root    false    216            �            1259    17993    notifications_id_seq    SEQUENCE     }   CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.notifications_id_seq;
       public          root    false    216            %           0    0    notifications_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;
          public          root    false    217            �            1259    16871    orders    TABLE     $  CREATE TABLE public.orders (
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
    DROP TABLE public.orders;
       public         heap    root    false            �            1259    16869    orders_id_seq    SEQUENCE     v   CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.orders_id_seq;
       public          root    false    207            &           0    0    orders_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;
          public          root    false    206            �            1259    17142    refreshtoken_id_seq    SEQUENCE     |   CREATE SEQUENCE public.refreshtoken_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE public.refreshtoken_id_seq;
       public          root    false            �            1259    17144    refreshtoken    TABLE     �   CREATE TABLE public.refreshtoken (
    id bigint DEFAULT nextval('public.refreshtoken_id_seq'::regclass) NOT NULL,
    expiry_date timestamp with time zone,
    current_token character varying(255),
    user_id bigint NOT NULL
);
     DROP TABLE public.refreshtoken;
       public         heap    root    false    214            '           0    0    TABLE refreshtoken    COMMENT     L   COMMENT ON TABLE public.refreshtoken IS 'information on the refresh token';
          public          root    false    215            (           0    0 !   COLUMN refreshtoken.current_token    COMMENT     P   COMMENT ON COLUMN public.refreshtoken.current_token IS 'current refresh token';
          public          root    false    215            )           0    0    COLUMN refreshtoken.user_id    COMMENT     G   COMMENT ON COLUMN public.refreshtoken.user_id IS 'owner of the token';
          public          root    false    215            �            1259    17072    roles    TABLE     c  CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY (ARRAY[('Admin'::character varying)::text, ('Manager'::character varying)::text, ('Supervisor'::character varying)::text, ('Charger'::character varying)::text, ('Scout'::character varying)::text])))
);
    DROP TABLE public.roles;
       public         heap    root    false            �            1259    17095    roles_id_seq    SEQUENCE     u   CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.roles_id_seq;
       public          root    false    211            *           0    0    roles_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;
          public          root    false    212            �            1259    16842    scooters    TABLE     �  CREATE TABLE public.scooters (
    id bigint NOT NULL,
    identifier character varying(255),
    status character varying(255),
    battery_level integer,
    zone bigint,
    speed_limit integer,
    CONSTRAINT scooters_status_check CHECK (((status)::text = ANY (ARRAY[('Active'::character varying)::text, ('Inactive'::character varying)::text, ('Blocked'::character varying)::text, ('Unblocked'::character varying)::text, ('Broken'::character varying)::text, ('Rented'::character varying)::text])))
);
    DROP TABLE public.scooters;
       public         heap    root    false            +           0    0    COLUMN scooters.identifier    COMMENT     E   COMMENT ON COLUMN public.scooters.identifier IS 'Unique identifier';
          public          root    false    205            ,           0    0    COLUMN scooters.zone    COMMENT     E   COMMENT ON COLUMN public.scooters.zone IS 'Location of the scooter';
          public          root    false    205            �            1259    16840    scooters_id_seq    SEQUENCE     x   CREATE SEQUENCE public.scooters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.scooters_id_seq;
       public          root    false    205            -           0    0    scooters_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.scooters_id_seq OWNED BY public.scooters.id;
          public          root    false    204            �            1259    17042    tasks    TABLE     �   CREATE TABLE public.tasks (
    order_id bigint NOT NULL,
    scooter_id bigint NOT NULL,
    priority integer NOT NULL,
    comment character varying(255)
);
    DROP TABLE public.tasks;
       public         heap    root    false            .           0    0    TABLE tasks    COMMENT     c   COMMENT ON TABLE public.tasks IS 'Intermediate table holding records linking Orders and Scooters';
          public          root    false    209            /           0    0    COLUMN tasks.order_id    COMMENT     =   COMMENT ON COLUMN public.tasks.order_id IS 'link to orders';
          public          root    false    209            0           0    0    COLUMN tasks.scooter_id    COMMENT     A   COMMENT ON COLUMN public.tasks.scooter_id IS 'link to scooters';
          public          root    false    209            1           0    0    COLUMN tasks.priority    COMMENT     C   COMMENT ON COLUMN public.tasks.priority IS '1 - highest priority';
          public          root    false    209            2           0    0    COLUMN tasks.comment    COMMENT     X   COMMENT ON COLUMN public.tasks.comment IS 'Description for completed or canceled task';
          public          root    false    209            �            1259    17057 
   user_roles    TABLE     ]   CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);
    DROP TABLE public.user_roles;
       public         heap    root    false            3           0    0    TABLE user_roles    COMMENT     c   COMMENT ON TABLE public.user_roles IS 'Intermediate table holding records linking User and Roles';
          public          root    false    210            �            1259    16808    users    TABLE     �  CREATE TABLE public.users (
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
    DROP TABLE public.users;
       public         heap    root    false            4           0    0    COLUMN users.password    COMMENT     �   COMMENT ON COLUMN public.users.password IS 'Should remain blank during the creation phase and will be defined upon the user''s first login.';
          public          root    false    203            5           0    0    COLUMN users.phone_number    COMMENT     I   COMMENT ON COLUMN public.users.phone_number IS 'Unique user identifier';
          public          root    false    203            6           0    0    COLUMN users.gender    COMMENT     V   COMMENT ON COLUMN public.users.gender IS 'Allowed values:
Male
Female
Not specified';
          public          root    false    203            7           0    0    COLUMN users.zone    COMMENT     D   COMMENT ON COLUMN public.users.zone IS 'predefined available zone';
          public          root    false    203            8           0    0    COLUMN users.shift    COMMENT     Q   COMMENT ON COLUMN public.users.shift IS 'Allowed values:
Day_shift
Night_shift';
          public          root    false    203            9           0    0    COLUMN users.created_by    COMMENT     G   COMMENT ON COLUMN public.users.created_by IS 'Creator of this record';
          public          root    false    203            :           0    0    COLUMN users.created_at    COMMENT     H   COMMENT ON COLUMN public.users.created_at IS 'Date of record creation';
          public          root    false    203            ;           0    0    COLUMN users.user_manager    COMMENT     G   COMMENT ON COLUMN public.users.user_manager IS 'Manager of this user';
          public          root    false    203            �            1259    16806    users_id_seq    SEQUENCE     u   CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.users_id_seq;
       public          root    false    203            <           0    0    users_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
          public          root    false    202            �            1259    16916    zones    TABLE     W   CREATE TABLE public.zones (
    id bigint NOT NULL,
    name character varying(255)
);
    DROP TABLE public.zones;
       public         heap    root    false            =           0    0    COLUMN zones.name    COMMENT     :   COMMENT ON COLUMN public.zones.name IS 'Predefined zone';
          public          root    false    208            �            1259    17097    zones_id_seq    SEQUENCE     u   CREATE SEQUENCE public.zones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.zones_id_seq;
       public          root    false    208            >           0    0    zones_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.zones_id_seq OWNED BY public.zones.id;
          public          root    false    213            W           2604    18194 
   history id    DEFAULT     h   ALTER TABLE ONLY public.history ALTER COLUMN id SET DEFAULT nextval('public.history_id_seq'::regclass);
 9   ALTER TABLE public.history ALTER COLUMN id DROP DEFAULT;
       public          root    false    219    218            T           2604    17995    notifications id    DEFAULT     t   ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);
 ?   ALTER TABLE public.notifications ALTER COLUMN id DROP DEFAULT;
       public          root    false    217    216            L           2604    17167 	   orders id    DEFAULT     f   ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);
 8   ALTER TABLE public.orders ALTER COLUMN id DROP DEFAULT;
       public          root    false    206    207    207            Q           2604    17280    roles id    DEFAULT     d   ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);
 7   ALTER TABLE public.roles ALTER COLUMN id DROP DEFAULT;
       public          root    false    212    211            J           2604    17304    scooters id    DEFAULT     j   ALTER TABLE ONLY public.scooters ALTER COLUMN id SET DEFAULT nextval('public.scooters_id_seq'::regclass);
 :   ALTER TABLE public.scooters ALTER COLUMN id DROP DEFAULT;
       public          root    false    205    204    205            F           2604    17350    users id    DEFAULT     d   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public          root    false    202    203    203            P           2604    17562    zones id    DEFAULT     d   ALTER TABLE ONLY public.zones ALTER COLUMN id SET DEFAULT nextval('public.zones_id_seq'::regclass);
 7   ALTER TABLE public.zones ALTER COLUMN id DROP DEFAULT;
       public          root    false    213    208                      0    18189    history 
   TABLE DATA                 public          root    false    218   �y                 0    17797    notifications 
   TABLE DATA                 public          root    false    216   �z                 0    16871    orders 
   TABLE DATA                 public          root    false    207   t{                 0    17144    refreshtoken 
   TABLE DATA                 public          root    false    215   �|                 0    17072    roles 
   TABLE DATA                 public          root    false    211   ~                 0    16842    scooters 
   TABLE DATA                 public          root    false    205   �~                 0    17042    tasks 
   TABLE DATA                 public          root    false    209   2                 0    17057 
   user_roles 
   TABLE DATA                 public          root    false    210   �                 0    16808    users 
   TABLE DATA                 public          root    false    203   >�                 0    16916    zones 
   TABLE DATA                 public          root    false    208   m�       ?           0    0    history_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.history_id_seq', 617, true);
          public          root    false    219            @           0    0    notifications_id_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('public.notifications_id_seq', 915, true);
          public          root    false    217            A           0    0    orders_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.orders_id_seq', 619, true);
          public          root    false    206            B           0    0    refreshtoken_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.refreshtoken_id_seq', 28, true);
          public          root    false    214            C           0    0    roles_id_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.roles_id_seq', 5, true);
          public          root    false    212            D           0    0    scooters_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.scooters_id_seq', 8, true);
          public          root    false    204            E           0    0    users_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.users_id_seq', 1345, true);
          public          root    false    202            F           0    0    zones_id_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.zones_id_seq', 2, true);
          public          root    false    213            j           2606    17526    tasks PK_Task 
   CONSTRAINT     _   ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT "PK_Task" PRIMARY KEY (order_id, scooter_id);
 9   ALTER TABLE ONLY public.tasks DROP CONSTRAINT "PK_Task";
       public            root    false    209    209            l           2606    17340    user_roles PK_UserRole 
   CONSTRAINT     d   ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "PK_UserRole" PRIMARY KEY (user_id, role_id);
 B   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT "PK_UserRole";
       public            root    false    210    210            n           2606    17282    roles PK_roles 
   CONSTRAINT     N   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT "PK_roles" PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.roles DROP CONSTRAINT "PK_roles";
       public            root    false    211            }           2606    18201    history history_pk 
   CONSTRAINT     P   ALTER TABLE ONLY public.history
    ADD CONSTRAINT history_pk PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.history DROP CONSTRAINT history_pk;
       public            root    false    218            x           2606    18003    notifications notifications_pk 
   CONSTRAINT     \   ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pk PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_pk;
       public            root    false    216            z           2606    18181 "   notifications notifications_unique 
   CONSTRAINT     m   ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_unique UNIQUE (order_id, scooter_id);
 L   ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_unique;
       public            root    false    216    216            b           2606    17193    orders orders_name_unique 
   CONSTRAINT     T   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_name_unique UNIQUE (name);
 C   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_name_unique;
       public            root    false    207            d           2606    17169    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            root    false    207            r           2606    17565    refreshtoken refresh_pk 
   CONSTRAINT     U   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_pk PRIMARY KEY (id);
 A   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT refresh_pk;
       public            root    false    215            t           2606    17260 !   refreshtoken refresh_unique_token 
   CONSTRAINT     e   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_unique_token UNIQUE (current_token);
 K   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT refresh_unique_token;
       public            root    false    215            v           2606    17268 #   refreshtoken refresh_unique_user_id 
   CONSTRAINT     a   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT refresh_unique_user_id UNIQUE (user_id);
 M   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT refresh_unique_user_id;
       public            root    false    215            p           2606    17297    roles roles_name_unique 
   CONSTRAINT     R   ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_unique UNIQUE (name);
 A   ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_name_unique;
       public            root    false    211            ^           2606    17318 #   scooters scooters_identifier_unique 
   CONSTRAINT     d   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_identifier_unique UNIQUE (identifier);
 M   ALTER TABLE ONLY public.scooters DROP CONSTRAINT scooters_identifier_unique;
       public            root    false    205            `           2606    17306    scooters scooters_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.scooters DROP CONSTRAINT scooters_pkey;
       public            root    false    205            Z           2606    17610    users users_phone_number_key 
   CONSTRAINT     _   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_number_key UNIQUE (phone_number);
 F   ALTER TABLE ONLY public.users DROP CONSTRAINT users_phone_number_key;
       public            root    false    203            \           2606    17352    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            root    false    203            f           2606    17461    zones zones_name unique 
   CONSTRAINT     T   ALTER TABLE ONLY public.zones
    ADD CONSTRAINT "zones_name unique" UNIQUE (name);
 C   ALTER TABLE ONLY public.zones DROP CONSTRAINT "zones_name unique";
       public            root    false    208            h           2606    17541    zones zones_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.zones
    ADD CONSTRAINT zones_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.zones DROP CONSTRAINT zones_pkey;
       public            root    false    208            {           1259    18207    history_created_at_idx    INDEX     P   CREATE INDEX history_created_at_idx ON public.history USING btree (created_at);
 *   DROP INDEX public.history_created_at_idx;
       public            root    false    218            �           2606    17822    tasks FK_Task_Order_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT "FK_Task_Order_Id" FOREIGN KEY (order_id) REFERENCES public.orders(id) ON DELETE CASCADE;
 B   ALTER TABLE ONLY public.tasks DROP CONSTRAINT "FK_Task_Order_Id";
       public          root    false    207    209    2916            �           2606    17827    tasks FK_Task_Scooter_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT "FK_Task_Scooter_Id" FOREIGN KEY (scooter_id) REFERENCES public.scooters(id) ON DELETE CASCADE;
 D   ALTER TABLE ONLY public.tasks DROP CONSTRAINT "FK_Task_Scooter_Id";
       public          root    false    209    205    2912            �           2606    17341    user_roles FK_UserRole_Role_Id    FK CONSTRAINT        ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_Role_Id" FOREIGN KEY (role_id) REFERENCES public.roles(id);
 J   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT "FK_UserRole_Role_Id";
       public          root    false    211    210    2926            �           2606    17731    user_roles FK_UserRole_User_Id    FK CONSTRAINT     �   ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT "FK_UserRole_User_Id" FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
 J   ALTER TABLE ONLY public.user_roles DROP CONSTRAINT "FK_UserRole_User_Id";
       public          root    false    203    210    2908            �           2606    17659 $   refreshtoken FK_refreshtoken_user_id    FK CONSTRAINT     �   ALTER TABLE ONLY public.refreshtoken
    ADD CONSTRAINT "FK_refreshtoken_user_id" FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
 P   ALTER TABLE ONLY public.refreshtoken DROP CONSTRAINT "FK_refreshtoken_user_id";
       public          root    false    2908    203    215            �           2606    18182 $   notifications notifications_tasks_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_tasks_fk FOREIGN KEY (order_id, scooter_id) REFERENCES public.tasks(order_id, scooter_id) ON DELETE CASCADE;
 N   ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_tasks_fk;
       public          root    false    2922    216    209    209    216            �           2606    17837 $   notifications notifications_users_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
 N   ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_users_fk;
       public          root    false    2908    216    203            �           2606    17664    orders orders_assigned_to_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id) ON DELETE CASCADE;
 H   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_assigned_to_fkey;
       public          root    false    2908    203    207            �           2606    17669    orders orders_created_by_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE CASCADE;
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_created_by_fkey;
       public          root    false    207    203    2908            �           2606    17679    orders orders_updated_by_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE CASCADE;
 G   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_updated_by_fkey;
       public          root    false    207    203    2908            �           2606    17567    scooters scooters_zones_fk    FK CONSTRAINT     v   ALTER TABLE ONLY public.scooters
    ADD CONSTRAINT scooters_zones_fk FOREIGN KEY (zone) REFERENCES public.zones(id);
 D   ALTER TABLE ONLY public.scooters DROP CONSTRAINT scooters_zones_fk;
       public          root    false    208    2920    205            �           2606    17751    users user_head_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_head_fk FOREIGN KEY (user_manager) REFERENCES public.users(id) ON DELETE SET DEFAULT;
 <   ALTER TABLE ONLY public.users DROP CONSTRAINT user_head_fk;
       public          root    false    203    203    2908                       2606    17756    users users_users_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_users_fk FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET DEFAULT;
 >   ALTER TABLE ONLY public.users DROP CONSTRAINT users_users_fk;
       public          root    false    203    203    2908            ~           2606    17774    users users_zones_fk    FK CONSTRAINT     p   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_zones_fk FOREIGN KEY (zone) REFERENCES public.zones(id);
 >   ALTER TABLE ONLY public.users DROP CONSTRAINT users_zones_fk;
       public          root    false    208    203    2920               �   x���AO�@�����\�H��,XXO��@b1i�c�V �@�1�����O3ɛ���I��z�gI��c�x���/�~h�{���_��Y��c�,��FR�#?y�.x��c��T3�ՠ��^*��:j�pS*Sh�,<�ʶ��x<hc%A�#�$LW6�Q�$Х����#d����A��D��Ú��+'�K:�@2�}
���e��7C����r�,�EW}��� ���?q�)�����>g8������q>vy         g   x���v
Q���W((M��L���/�L�LN,���+Vs�	uV�0�Q037�QP7202�54�54V04�2��20�3SmuCK�b�PMk... a�E           x���Mk�0�����c��dٱ�S)=��v��5]a�#�����3��m l�~�^����ۙ(�ٍx~��,�^��w�r>����H���!���)�ռ,�B�;�\(P{ecH��\��4l<�4��{���Q�l�Oa��y�^\%�YD�=Du�yDϹ� ���g�?�PE�B�;'4�c��(�5n����_`��M��ߺ�gd�֨���B}�i\�E�+w��;dN��������ܟ)A�c���5Y{~������         |  x���MkA�����E�곫z<y�aA"��{�l�����6!G����oUon�?�MǛ��ӯ����:?���������c���������t��
@>1/�a�M��ޠ^� �V.j'hkP��-@��F4��c{��p�_����(ケ��<\׳%I%���6q�Ӷc�V�zУ\.��Eg��|�XXf�^�:�v�l �	� KM��b+^�w�^"�'�hԞ�.��� �Y�ޙ
%�2�ў�W��*;��\"�����yt&w����V5)ae�1�Z�e8tύ��Iv��>��ق�X�\�9�n-��3v�Ec�TǬ���nA�#wɒ�_���KVZ�α���a���(�W5P�����d�=}�9�����&;�)         h   x���v
Q���W((M��L�+��I-Vs�	uV�0�QPwL���S״��$����71/1=��(��@����Ee����i1jq�H,"�
S����% �\\ SCG�         �   x���v
Q���W((M��L�+N��/I-*Vs�	uV�0�QP76��P�N9��٩)@�����������5�'��M13��\�Y�
d�@1%�#�!�(�X J��i�	P��	� =jh@�SL����P�3�!���A�� �Ro�         z   x���v
Q���W((M��L�+I,�.Vs�	uV�0��Q0�Q0�Q���Ѵ��$�����DF`D�#��0"�rt�V&`�$�a����Bf�Br~nANjIj�:�� ��         r   x���v
Q���W((M��L�+-N-�/��I-Vs�	uV�0�Q0Դ��$J�������	���tL�VljlL��F���&�)F@��ڜ�04�$�ݖ����ژ$� �pq �l��           x���K��J���)XL�7��t7M�w�eE@ؤP��<TDe>}�$3Ne&�U���>��qΑU]��)Y�O�]���]��!�̎b�:� �j���{��J5����G�<f��U��;��C�r6���<#7Y.U�"8G'�����vk��%�Ri �a ����{/ʷ+�!�&`�S ��ƄF��̰�-�}��A�W)髬8���$<�W�Y�����07��tD���D��k#���;��lu����n�
!y�-F��4�z�C��P�ic�f�r<�w�7��x�����Zr�E��P�Y��[[�Q���^��ѐH�rҕ5<�������Fh���p����q��Y��N´ٍ�t�7��;fZ*�1���n�@�Ym����/�%-�jg��O�� �S8G| �z}�wy��\��z�[V;��+O�6�� �T��ĕR��k�#bo�F�ez��v�5�GђN�b���`�̠̪�����á���׺"�ƀ��lABU�`I�-D@��BB݌b�Na���.�h��<�E"�����$��F��7������y־D����� �5]�t�_��7X�U5�iawzQ�����^C�K~QR��
z�B0F��d��n0}R�-�X+�,�����J����{ �ی��#/� ���~�E���+O�=����QK��e/O{�(Ӻ��[����t��x��
xv_����� ���{��z���^��\+:�m�,�g���:��֕XZ����N��������7�7ߒ         E   x���v
Q���W((M��L֫��K-Vs�	uV�0�QP�20T״��$���ֈ(��`�� �\\ Pu%�     