<template>
  <Editor :value="value" :plugins="plugins" @change="handleChange" />
</template>

<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm";
import { Editor } from "@bytemd/vue-next";
import highlight from "@bytemd/plugin-highlight";
import { defineProps, withDefaults } from "vue";

const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
];

interface Props {
  value: string;
  handleChange: (v: string) => void;
}

const props = withDefaults(defineProps<Props>(), {
  currentCode: () => "",
  handleChange: (v: string) => {
    console.log(v);
  },
});
</script>

<style>
.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child svg {
  display: none;
}
</style>
