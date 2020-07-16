<template>
  <div>
    <a-card title="Default size card" style="width: 300px">
      <a href="#" slot="extra">more</a>
      <p>card content</p>
      <p>card content</p>
      <p>card content</p>
    </a-card>
    <br />
    <a-card size="small" title="Small size card" style="width: 300px">
      <a href="#" slot="extra">more</a>
      <p>card content</p>
      <p>card content</p>
      <p>card content</p>
    </a-card>
  </div>
</template>

<script>
    export default {
        name: "MonitorList"
    }
</script>

<style scoped>

</style>