import { RouteRecordRaw } from "vue-router";
import NoAuthView from "@/views/NoAuthView.vue";
import { roleEnum } from "@/components/scripts/access/roleEnum";
import UserView from "@/views/user/UserView.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UserUpdatePasswordView from "@/views/user/UserUpdatePasswordView.vue";
import ProblemAddView from "@/views/problem/ProblemModifyView.vue";
import ProblemListView from "@/views/problem/ProblemListView.vue";
import ProblemDetailsView from "@/views/problem/ProblemDetailsView.vue";
import ProblemDetailsSolutionView from "@/views/problem/ProblemDetailsSolutionView.vue";
import ProblemDetailsCommentsView from "@/views/problem/ProblemDetailsCommentsView.vue";
import ProblemDetailsSubmissionsView from "@/views/problem/ProblemDetailsSubmissionsView.vue";
import UserProfileView from "@/views/user/UserProfileView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/problem/list",
    name: "题目列表",
    component: ProblemListView,
    meta: {
      access: roleEnum.NOT_LOGIN,
      basicLayout: true,
    },
  },
  {
    path: "/problem/details/:id",
    name: "题目详情",
    component: ProblemDetailsView,
    props: true,
    meta: {
      access: roleEnum.NOT_LOGIN,
      hideInMenu: true,
    },
    children: [
      {
        path: "/problem/details/:id/solution",
        name: "题目解法",
        component: ProblemDetailsSolutionView,
        meta: {
          access: roleEnum.NOT_LOGIN,
          hideInMenu: true,
        },
      },
      {
        path: "/problem/details/:id/comments",
        name: "题目评论区",
        component: ProblemDetailsCommentsView,
        props: true,
        meta: {
          access: roleEnum.NOT_LOGIN,
          hideInMenu: true,
        },
      },
      {
        path: "/problem/details/:id/submissions",
        name: "题目提交历史",
        component: ProblemDetailsSubmissionsView,
        props: true,
        meta: {
          access: roleEnum.DEFAULT_USER,
          hideInMenu: true,
        },
      },
    ],
  },
  {
    path: "/problem/add",
    name: "添加题目",
    component: ProblemAddView,
    meta: {
      access: roleEnum.ADMIN,
      hideInMenu: true,
    },
  },
  {
    path: "/user/profile",
    name: "用户信息更改",
    component: UserProfileView,
    meta: {
      access: roleEnum.DEFAULT_USER,
      basicLayout: true,
      hideInMenu: true,
    },
  },
  {
    path: "/user",
    name: "用户",
    component: UserView,
    meta: {
      access: roleEnum.DEFAULT_USER,
      basicLayout: true,
      hideInMenu: true,
    },
  },
  {
    path: "/user/login",
    name: "用户登录",
    component: UserLoginView,
    meta: {
      access: roleEnum.NOT_LOGIN,
      hideInMenu: true,
      basicLayout: true,
    },
  },

  {
    path: "/user/register",
    name: "用户注册",
    component: UserRegisterView,
    meta: {
      access: roleEnum.NOT_LOGIN,
      hideInMenu: true,
      basicLayout: true,
    },
  },
  {
    path: "/user/password",
    name: "用户修改密码",
    component: UserUpdatePasswordView,
    meta: {
      access: roleEnum.NOT_LOGIN,
      hideInMenu: true,
      basicLayout: true,
    },
  },
  {
    path: "/",
    redirect: "/problem/list",
    meta: {
      access: roleEnum.NOT_LOGIN,
      hideInMenu: true,
    },
  },
  {
    path: "/problem",
    redirect: "/problem/list",
    meta: {
      access: roleEnum.NOT_LOGIN,
      hideInMenu: true,
    },
  },
  {
    path: "/noAuth",
    name: "无授权",
    component: NoAuthView,
    meta: {
      hideInMenu: true,
      basicLayout: true,
    },
  },
];
